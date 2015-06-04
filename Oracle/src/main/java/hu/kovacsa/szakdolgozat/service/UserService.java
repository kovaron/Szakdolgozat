package hu.kovacsa.szakdolgozat.service;

import hu.kovacsa.szakdolgozat.model.UserListWrapper;
import hu.kovacsa.szakdolgozat.model.UserQueryData;

/**
 * Common User service interface
 *
 * @author Aron Kovacs
 */
public interface UserService {

	/** Returns all the users in the system */
	UserListWrapper findAll();

	/** Finds a user by it's user_id */
	Object findById(final Long id);

	/** Returns all users in the specified state */
	UserListWrapper findByState(final String state);

	/** Finds the shortest path between the two users by their user_id */
	UserQueryData shortestPathBetween(final Long startId, final Long endId, final Boolean needProperties);

	/** Counts all the users in the system */
	UserListWrapper countAll();

	/** Returns users related to the given one until a specified level */
	UserListWrapper findRelatedByLevel(final Long id, final Integer to);

}
