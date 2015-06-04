package hu.kovacsa.szakdolgozat.config;

import hu.kovacsa.szakdolgozat.service.UserService;
import hu.kovacsa.szakdolgozat.service.impl.OrientDBUserServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * Created by Aron Kovacs.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "hu.kovacsa.szakdolgozat.service.impl", "hu.kovacsa.szakdolgozat.rest" })
public class AppConfig {

	@Value("${orientdb.url}")
	private String url;

	@Value("${orientdb.user}")
	private String user;

	@Value("${orientdb.password}")
	private String password;

	@Bean
	public OrientGraph orientGraph() {
		return new OrientGraph(url, user, password,  true);
	}

	@Bean
	public UserService userService() {
		return new OrientDBUserServiceImpl();
	}

}
