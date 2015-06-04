package hu.kovacsa.szakdolgozat.config;

import hu.kovacsa.szakdolgozat.service.UserService;
import hu.kovacsa.szakdolgozat.service.impl.Neo4jSpringDataUserServiceImpl;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.rest.SpringRestGraphDatabase;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by Aron Kovacs.
 */
@Configuration
@EnableWebMvc
@EnableNeo4jRepositories(basePackages = "hu.kovacsa.szakdolgozat.repository")
@ComponentScan(basePackages = { "hu.kovacsa.szakdolgozat.service.impl", "hu.kovacsa.szakdolgozat.rest" })
public class AppConfig extends Neo4jConfiguration {

	@Value("${neo4j.url}")
	private final String url = "http://localhost:7474/db/data";

	AppConfig() {
		setBasePackage("hu.kovacsa.szakdolgozat.model");
	}

	@Bean(name = "graphDatabaseService")
	@Override
	public GraphDatabaseService getGraphDatabaseService() {
		return new SpringRestGraphDatabase(url, "neo4j", "password");
	}

	@Bean
	public UserService userService() {
		return new Neo4jSpringDataUserServiceImpl();
	}

}
