package hu.kovacsa.szakdolgozat.service.impl;

import hu.kovacsa.szakdolgozat.model.ParentOf;
import hu.kovacsa.szakdolgozat.model.User;
import hu.kovacsa.szakdolgozat.model.UserListWrapper;
import hu.kovacsa.szakdolgozat.model.UserQueryData;
import hu.kovacsa.szakdolgozat.service.UserService;

import java.util.List;
import java.util.Set;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * NEO4 native Java API based {@link UserService} implementation.
 *
 * @author Aron Kovacs
 */
@Service
public class Neo4jNativeUserServiceImpl implements UserService {

	@Autowired
	private GraphDatabaseService gds;

	@Override
	public UserListWrapper findAll() {
		final List<User> users = Lists.newArrayList();
		final long duration;
		try (Transaction tx = gds.beginTx()) {
			final long start = System.nanoTime();
			final Iterable<Node> nodes = getAll();
			duration = System.nanoTime() - start;
			nodes.forEach(n -> {
				users.add(UserFactory.createUser(n));
			});
			tx.success();
		}
		return new UserListWrapper(duration, users);
	}

	@Override
	public Object findById(final Long id) {
		return findByProperty(User.USER_ID, id, true);
	}

	@Override
	public UserListWrapper findByState(final String state) {
		return findByProperty(User.STATE, state, false);
	}

	@Override
	public UserQueryData shortestPathBetween(final Long startUserId, final Long endUserId, final Boolean needProperties) {
		try (final Transaction tx = gds.beginTx()) {
			final Label label = () -> User.LABEL_NAME;

			final long start1 = System.nanoTime();
			final ResourceIterable<Node> nodes = gds.findNodesByLabelAndProperty(label, User.USER_ID, startUserId);
			final long duration1 = System.nanoTime() - start1;
			if (!nodes.iterator().hasNext()) {
				return new UserQueryData();
			}
			final Node startNode = nodes.iterator().next();

			final long start2 = System.nanoTime();
			final ResourceIterable<Node> endNodes = gds.findNodesByLabelAndProperty(label, User.USER_ID, endUserId);
			final long duration2 = System.nanoTime() - start2;
			if (!endNodes.iterator().hasNext()) {
				return new UserQueryData();
			}
			final Node endNode = endNodes.iterator().next();

			final long start3 = System.nanoTime();
			final PathFinder<Path> finder = GraphAlgoFactory
					.shortestPath(PathExpanders
							.forTypeAndDirection(new ParentOf(), Direction.BOTH), Integer.MAX_VALUE);

			final Path path = finder.findSinglePath(startNode, endNode);
			final long duration3 = System.nanoTime() - start3;

			final List<User> users = Lists.newArrayList();
			final UserQueryData queryData = new UserQueryData();
			if(path != null && path.nodes() != null){
				path.nodes().forEach(n -> {
					users.add(UserFactory.createUser(n, needProperties));
				});
				queryData.setNodes(Lists.newArrayList(users));
			}

			queryData.setStart(UserFactory.createUser(startNode, needProperties));
			queryData.setEnd(UserFactory.createUser(endNode, needProperties));
			queryData.setLength(users.size());
			queryData.setSearchTime(duration1 + duration2 + duration3);
			return queryData;
		}
	}

	@Override
	public UserListWrapper countAll() {
		final long duration;
		int count;
		try (Transaction tx = gds.beginTx()) {
			final long start = System.nanoTime();
			final Iterable<Node> nodes = getAll();
			duration = System.nanoTime() - start;
			count = IteratorUtil.count(nodes);
			tx.success();
		}
		final UserListWrapper wrapper = new UserListWrapper();
		wrapper.setTime(duration);
		wrapper.setCount(count);
		return wrapper;
	}

	/**
	 * Returns an {@link Iterable} instance of all users in the system.
	 *
	 * @return
	 */
	private Iterable<Node> getAll() {
		return GlobalGraphOperations.at(gds).getAllNodes();
	}

	/**
	 * Finds one or multiple users (depending one the parameter - onlyOne) by
	 * property key and value.
	 *
	 * @param key
	 * @param value
	 * @param onlyOne
	 * @return
	 */
	private UserListWrapper findByProperty(final String key, final Object value, final boolean onlyOne) {
		try (Transaction tx = gds.beginTx()) {
			final Label label = () -> User.LABEL_NAME;

			final Long start = System.nanoTime();
			final ResourceIterable<Node> nodes = gds.findNodesByLabelAndProperty(label, key, value);
			final long duration = System.nanoTime() - start;

			// if there is no result, return an empty wrapper with search time
			// only.
			if (!nodes.iterator().hasNext()) {
				return new UserListWrapper(duration, null);
			}
			final List<User> users = Lists.newArrayList();
			if (onlyOne) {
				final Node n = nodes.iterator().next();
				users.add(UserFactory.createUser(n));
			} else {
				nodes.forEach(n -> {
					users.add(UserFactory.createUser(n));
				});
			}
			tx.success();
			return new UserListWrapper(duration, users);
		}
	}

	@Override
	public UserListWrapper findRelatedByLevel(final Long id, final Integer to) {
		try (final Transaction tx = gds.beginTx()) {
			final Label label = () -> User.LABEL_NAME;
			final long start1 = System.nanoTime();
			final ResourceIterable<Node> nodes = gds.findNodesByLabelAndProperty(label, User.USER_ID, id);
			final long duration1 = System.nanoTime() - start1;
			if (!nodes.iterator().hasNext()) {
				return new UserListWrapper();
			}
			final Node startNode = nodes.iterator().next();
			final long start2 = System.nanoTime();
			final Traverser traverser = gds.traversalDescription()
					.breadthFirst()
					.evaluator(Evaluators.toDepth(to - 1))
					.uniqueness(Uniqueness.NODE_GLOBAL)
					.relationships(new ParentOf(), Direction.OUTGOING)
					.traverse(startNode);

			final long duration2 = System.nanoTime() - start2;
			final Set<User> users = Sets.newHashSet();
			traverser.nodes().forEach(n -> {
				users.add(UserFactory.createUser(n));
			});

			return new UserListWrapper(duration1 + duration2, Lists.newArrayList(users));
		}
	}

	/**
	 * Static factory class to transform {@link Node} into {@link User}
	 *
	 * @author kovaron
	 *
	 */
	private static class UserFactory {

		public static User createUser(final Node n) {
			return createUser(n, true);
		}

		/**
		 * Node -> User transformation.
		 *
		 * @param n
		 * @return
		 */
		public static User createUser(final Node n, final boolean needProperties) {
			final User u = new User();
			u.setUserId((Long) n.getProperty(User.USER_ID));
			if(needProperties){
				u.setDateOfBirth(Long.valueOf((String) n.getProperty(User.DATE_OF_BIRTH)));
				u.setLastYearIncome((Long) n.getProperty(User.LAST_YEAR_INCOME));
				u.setParentId(Long.valueOf((String) n.getProperty(User.PARENT_ID)));
				u.setState((String) n.getProperty(User.STATE));
			}
			return u;
		}
	}
}
