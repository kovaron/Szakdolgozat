package hu.kovacsa.szakdolgozat.repository;

import hu.kovacsa.szakdolgozat.model.User;

import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * NEO4J Spring-Data repository
 *
 * @author Aron Kovacs
 */
@Repository
public interface Neo4jUserRepository extends GraphRepository<User> {

	String PATH = "path";

	/**
	 * Returns the shortest path between two users.
	 *
	 * @param startId
	 * @param endId
	 * @return
	 */
	@Query(	"match(" +
			"start:User{user_id:{0}})," +
			"(end:User {user_id:{1}})," +
			"path=(start)-[:PARENT_OF*]-(end)"+
			" return " + PATH)
	// note: path must remain at the end!
	Result<Map<String, Object>> findShortestPathBetween(final Long startId, final Long endId);

	/**
	 * Returns a user by it's user id.
	 *
	 * @param userId
	 * @return
	 */
	User findByUserId(final Long userId);

	/**
	 * Returns every user in a specified state.
	 *
	 * @param allam
	 * @return
	 */
	Iterable<User> findByState(final String state);

}
