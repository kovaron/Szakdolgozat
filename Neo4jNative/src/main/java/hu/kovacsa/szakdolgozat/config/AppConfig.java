package hu.kovacsa.szakdolgozat.config;

import hu.kovacsa.szakdolgozat.service.UserService;
import hu.kovacsa.szakdolgozat.service.impl.Neo4jNativeUserServiceImpl;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by Aron Kovacs.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "hu.kovacsa.szakdolgozat.service.impl", "hu.kovacsa.szakdolgozat.rest" })
public class AppConfig {

	@Value("${neo4j.url}")
	private String url;

	@Bean(name = "graphDatabaseService")
	public GraphDatabaseService graphDatabaseService() {
		final GraphDatabaseService ds = new GraphDatabaseFactory()
		.newEmbeddedDatabase(url);
		registerShutDownHook(ds);
		return ds;
	}

	@Bean
	public UserService userService() {
		return new Neo4jNativeUserServiceImpl();
	}

	/**
	 * Creates a shutdown hook to correctly stop Neo4j embedded database It will
	 * shut down correctly the graph database even if the program is
	 * interrupted.
	 *
	 * @param ds
	 */
	private void registerShutDownHook(final GraphDatabaseService ds) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				ds.shutdown();
			}
		});
	}

}
