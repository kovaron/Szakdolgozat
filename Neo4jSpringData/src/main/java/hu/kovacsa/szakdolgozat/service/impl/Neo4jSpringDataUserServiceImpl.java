package hu.kovacsa.szakdolgozat.service.impl;

import hu.kovacsa.szakdolgozat.model.ParentOf;
import hu.kovacsa.szakdolgozat.model.User;
import hu.kovacsa.szakdolgozat.model.UserListWrapper;
import hu.kovacsa.szakdolgozat.model.UserQueryData;
import hu.kovacsa.szakdolgozat.repository.Neo4jUserRepository;
import hu.kovacsa.szakdolgozat.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.kernel.Traversal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.conversion.ResultConverter;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.transaction.Neo4jTransactional;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

/**
 * NEO4J Spring-Data {@link UserService} implementation.
 *
 * @author Aron Kovacs
 */
@SuppressWarnings("deprecation")
@Service
@Neo4jTransactional
@Configurable
public class Neo4jSpringDataUserServiceImpl implements UserService {

	@Autowired
	private Neo4jUserRepository repository;

	@Autowired
	private GraphDatabase graphDatabase;

	@SuppressWarnings("unchecked")
	@Override
	public UserListWrapper findAll() {
		final UserListWrapper ret = new UserListWrapper();
		final long start = System.nanoTime();
		final Result<User> results = repository.findAll();
		final long duration = System.nanoTime() - start;
		ret.setTime(duration);
		ret.setResults(results.to(User.class).as(List.class));
		ret.setCount(ret.getResults().size());
		return ret;
	}

	@Override
	public Object findById(final Long id) {
		final long start = System.nanoTime();
		final Object o = repository.findByUserId(id);
		final long duration = System.nanoTime() - start;
		return new UserListWrapper(duration, Arrays.asList(o));
	}

	@Override
	public UserListWrapper findByState(final String state) {
		final UserListWrapper ret = new UserListWrapper();
		final List<Object> list = new ArrayList<>();
		final Long start = System.nanoTime();
		final Iterable<User> users = repository.findByState(state);
		final long duration = System.nanoTime() - start;
		for (final User u : users) {
			u.setRelated(null);
			list.add(u);
		}
		ret.setResults(list);
		ret.setTime(duration);
		ret.setCount(list.size());
		return ret;
	}

	@Override
	public UserQueryData shortestPathBetween(final Long startUserId, final Long endUserId, final Boolean needProperties) {
		final Long start = System.nanoTime();
		final Result<Map<String, Object>> users = repository.findShortestPathBetween(startUserId, endUserId);
		final Long duration = System.nanoTime() - start;
		final UserQueryData userQueryData = new PathConverter(needProperties, duration).convert(users.single(),
				UserQueryData.class);
		userQueryData.setSearchTime(duration);
		return userQueryData;
	}

	@Override
	public UserListWrapper countAll() {
		final long start = System.nanoTime();
		final long count = repository.count();
		final long duration = System.nanoTime() - start;
		final UserListWrapper ret = new UserListWrapper();
		ret.setCount((int) count);
		ret.setTime(duration);
		return ret;
	}

	/**
	 * Converts the Query's result Map into JSON processable UserQueryData
	 * format.
	 */
	private class PathConverter extends ResultConverter.ResultConverterAdapter<Map<String, Object>, UserQueryData> {

		private static final String LENGTH = "length";
		private static final String START = "start";
		private static final String END = "end";
		private static final String NODES = "nodes";
		private final Boolean needProperties;

		@SuppressWarnings("unused")
		private long duration = 0;

		public PathConverter(final Boolean needProperties, final long duration) {
			this.needProperties = needProperties;
			this.duration = duration;
		}

		@Override
		@SuppressWarnings("unchecked")
		public UserQueryData convert(final Map<String, Object> value, final Class<UserQueryData> type) {
			final HashMap<String, Object> o = (HashMap<String, Object>) value.get(Neo4jUserRepository.PATH);
			return createFromQuery(o);
		}

		@SuppressWarnings("rawtypes")
		private UserQueryData createFromQuery(final Map<String, Object> result) {
			final UserQueryData ret = new UserQueryData();
			ret.setLength((Integer) result.get(LENGTH));
			ret.setStart(userFromObject(result.get(START)));
			ret.setEnd(userFromObject(result.get(END)));
			final List<User> nodes = new ArrayList<>();
			for (final Object listElement : (List) result.get(NODES)) {
				nodes.add(userFromObject(listElement));
			}
			ret.setNodes(Arrays.asList(nodes.toArray()));
			return ret;
		}

		/**
		 * Parses a User entity from it's object representation: ex.
		 * http://localhost:7474/db/data/node/1
		 *
		 * @param o
		 * @return
		 */
		private User userFromObject(final Object o) {
			final String id = ((String) o).split("/data/node/")[1];
			final long start = System.nanoTime();
			final User user = repository.findOne(Long.valueOf(id));
			duration += System.nanoTime() - start;
			if (!Boolean.TRUE.equals(needProperties)) {
				user.setDateOfBirth(null);
				user.setRelated(null);
				user.setLastYearIncome(null);
				user.setState(null);
			}
			return user;
		}
	}

	@Override
	public UserListWrapper findRelatedByLevel(final Long id, final Integer to) {
		final User startUser = repository.findByUserId(id);
		final TraversalDescription traversal = Traversal.description().relationships(new ParentOf())
				.uniqueness(Uniqueness.NODE_GLOBAL).evaluator(Evaluators.toDepth(to));
		final long start = System.nanoTime();
		final Iterable<User> list = repository.findAllByTraversal(startUser, traversal);
		final long duration = System.nanoTime() - start;
		return new UserListWrapper(duration, Lists.newArrayList(list));
	}
}
