package hu.kovacsa.szakdolgozat.service.impl;

import hu.kovacsa.szakdolgozat.model.User;
import hu.kovacsa.szakdolgozat.model.UserJsonModel;
import hu.kovacsa.szakdolgozat.model.UserListWrapper;
import hu.kovacsa.szakdolgozat.model.UserQueryData;
import hu.kovacsa.szakdolgozat.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.traverse.OTraverse;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.filter.OSQLPredicate;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientDynaElementIterable;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * NEO4 native Java API based {@link UserService} implementation.
 *
 * @author Aron Kovacs
 */
@Service
public class OrientDBUserServiceImpl implements UserService {

	private static final String OUT = "out";
	private static final String FETCH_PLAN = "fetchPlan:";
	private static final String SHORTEST_PATH = "select flatten(sp) from (select shortestpath(%s,%s,'BOTH') as sp)";

	@Autowired
	private OrientGraph orientGraph;

	@Override
	public UserListWrapper findAll() {

		final long start = System.nanoTime();
		final Iterable<Vertex> vertices = orientGraph.getVerticesOfClass(User.LABEL_NAME);
		final long duration = System.nanoTime() - start;

		final List<User> users = Lists.newArrayList(vertices).stream()
				.map(new VertexToUserFunction())
				.collect(Collectors.toList());

		return new UserListWrapper(duration, users);
	}

	@Override
	public Object findById(final Long id) {
		return findByProperty(User.USER_ID, id);
	}

	@Override
	public UserListWrapper findByState(final String state) {
		return findByProperty(User.STATE, state);
	}

	@Override
	public UserQueryData shortestPathBetween(final Long startUserId, final Long endUserId, final Boolean needProperties) {
		final long start1 = System.nanoTime();
		final Vertex start = orientGraph.getVertexByKey(User.LABEL_NAME + "." + User.USER_ID, startUserId);
		final Vertex end =  orientGraph.getVertexByKey(User.LABEL_NAME + "." + User.USER_ID, endUserId);
		final long duration1 = System.nanoTime() - start1;

		final String query = String.format(SHORTEST_PATH, start.getId(), end.getId());
		final OCommandRequest req = orientGraph.command(new OSQLSynchQuery<Object>(query));

		final long start2 = System.nanoTime();
		final OrientDynaElementIterable it = req.execute();
		final long duration2 = System.nanoTime() - start2;

		final List<Object> users = Lists.newArrayList();

		while(it.iterator().hasNext()){
			users.add(new VertexToUserFunction(needProperties).apply((OrientVertex)it.iterator().next()));
		}

		final UserQueryData ret = new UserQueryData();
		ret.setStart(new VertexToUserFunction().apply(start));
		ret.setEnd(new VertexToUserFunction().apply(end));
		ret.setNodes(users);
		ret.setLength(users.size());
		ret.setSearchTime(duration1 + duration2);
		return ret;
	}

	@Override
	public UserListWrapper countAll() {
		final long start = System.nanoTime();
		final long count = orientGraph.countVertices(User.LABEL_NAME);
		final long duration = System.nanoTime() - start;
		final UserListWrapper ret = new UserListWrapper();
		ret.setTime(duration);
		ret.setCount((int)count);
		return ret;
	}

	@Override
	public UserListWrapper findRelatedByLevel(final Long id, final Integer to) {

		final OrientVertex from = (OrientVertex) orientGraph.getVertexByKey(User.LABEL_NAME + "." + User.USER_ID, id);
		final String idString = "#" + from.getIdentity().getClusterId() + ":" + from.getIdentity().getClusterPosition();
		final List<Object> users = Lists.newArrayList();
		final long start = System.nanoTime();
		final OTraverse traverse = orientGraph.traverse()
				.field(OUT)
				.predicate(new OSQLPredicate("$depth <= " + to))
				.target(new ORecordId(idString));
		long duration = System.nanoTime() - start;
		UserJsonModel model = null;
		while (traverse.hasNext()){
			final long start2 = System.nanoTime();
			final OIdentifiable oID = traverse.next();

			final String asJson = ((ODocument)oID).toJSON(FETCH_PLAN + "*:" + (to + 1));
			duration += System.nanoTime() - start2;

			try {
				model = new ObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true).readValue(asJson, UserJsonModel.class);
			} catch (final IOException e) {
				e.printStackTrace();
			}

			addToList(users, model);

		}
		return new UserListWrapper(duration, users);
	}

	/**
	 * Adds recursively all {@link Users} to the list.
	 * @param users
	 * @param model
	 */
	private void addToList(final List<Object> users, final UserJsonModel model){
		if(!CollectionUtils.isEmpty(model.getChildren())){
			for(final UserJsonModel.OutParentOf p : model.getChildren()){
				for(final UserJsonModel u : p.getNodes()){
					addToList(users, u);
				}
			}
		}
		if(model.getUserId() != null){
			users.add(new JsonToUserModelFunction().apply(model));
		}
	}

	/**
	 * Returns Users by a property 'key' which has the value 'value'.
	 * @param key
	 * @param value - Object
	 * @return
	 */
	private UserListWrapper findByProperty(final String key, final Object value){
		final long start = System.nanoTime();
		final Iterable<Vertex> v = orientGraph.getVertices(key, value);
		final long duration = System.nanoTime() - start;
		final List<Object> u =  Lists.newArrayList(v).stream()
				.map(new VertexToUserFunction())
				.collect(Collectors.toList());
		return new UserListWrapper(duration, u);
	}

	/**
	 * {@link Function} implementation transforming
	 * a {@link Vertex} object to {@link User} object.
	 *
	 * @author Aron Kovacs
	 */
	private class VertexToUserFunction implements Function<Vertex, User> {

		private final boolean needProperties;

		public VertexToUserFunction(){
			needProperties = true;
		}

		public VertexToUserFunction(final boolean needProperties){
			this.needProperties = needProperties;
		}

		@Override
		public User apply(final Vertex t) {
			final User u = new User();
			if(needProperties){
				u.setUserId(t.getProperty(User.USER_ID));
				u.setParentId(t.getProperty(User.PARENT_ID));
				u.setDateOfBirth(t.getProperty(User.DATE_OF_BIRTH));
				u.setLastYearIncome(t.getProperty(User.LAST_YEAR_INCOME));
				u.setState(t.getProperty(User.STATE));
			}
			return u;
		}

	}

	/**
	 * {@link Function} implementation transforming
	 * a {@link UserJsonModel} to {@link User}
	 *
	 * @author Aron Kovacs
	 */
	private class JsonToUserModelFunction implements Function<UserJsonModel, User>{

		@Override
		public User apply(final UserJsonModel t) {
			final User user = new User();
			user.setId(t.getId());
			user.setUserId(t.getUserId());
			user.setParentId(t.getParentId());
			user.setDateOfBirth(t.getDateOfBirth());
			user.setLastYearIncome(t.getLastYearIncome());
			user.setState(t.getState());
			return user;
		}

	}

}
