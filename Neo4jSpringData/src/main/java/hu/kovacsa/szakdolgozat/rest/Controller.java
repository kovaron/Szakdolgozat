package hu.kovacsa.szakdolgozat.rest;

import hu.kovacsa.szakdolgozat.model.UserListWrapper;
import hu.kovacsa.szakdolgozat.model.UserQueryData;

import org.springframework.web.bind.annotation.PathVariable;

/**
 * Rest controller interface definition
 *
 * @author Aron Kovacs
 */
public interface Controller {

	/** Returns all users in a wrapper format. */
	public UserListWrapper findAll();

	/** Finds a user by it's user_id */
	public Object findById(@PathVariable final Long id);

	/** Returns the shortest path between two users */
	public UserQueryData findShortestPathBetween(final Long startId, final Long endId, final Boolean needProperties);

	/** Lists all users in the specified state */
	public UserListWrapper findByState(@PathVariable final String state);

	/** Counts all users */
	public UserListWrapper countAll();

	/**
	 * Finds users related to the given one until the given level. If from and
	 * to are not specified, returns all the users. On Oracle databases it works
	 * until 5 levels only.
	 *
	 * @param id
	 *            user_id
	 * @param to
	 */
	public UserListWrapper findRelatedByLevel(@PathVariable final Long id, @PathVariable final Integer to);

}
