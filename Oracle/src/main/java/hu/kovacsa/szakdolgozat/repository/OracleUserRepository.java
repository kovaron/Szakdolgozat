package hu.kovacsa.szakdolgozat.repository;

import hu.kovacsa.szakdolgozat.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OracleUserRepository extends JpaRepository<User, Long> {

	String PATH = "path";

	/**
	 * Returns the shortest path between two users.
	 *
	 * @param startId
	 * @param endId
	 * @return
	 */
	// @Query("match(start:User{userId:{0}}),(end:User {userId:{1}}),path=(start)-[:PARENT_OF*]->(end)"
	// + " return "
	// + PATH)
	// note: path must remain at the end!
	// Result<Map<String, Object>> findShortestPathBetween(final long startId,
	// final long endId);

	/**
	 * Returns a user by it's user id.
	 *
	 * @param userId
	 * @return
	 */
	// User findById(final Long userId);

	List<User> findRelatedByLevel(final int level, final long id);

	/**
	 * Returns every user in a specified state.
	 *
	 * @param allam
	 * @return
	 */
	List<User> findByState(final String state);

}
