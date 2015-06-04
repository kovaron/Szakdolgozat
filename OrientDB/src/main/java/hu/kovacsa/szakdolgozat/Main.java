package hu.kovacsa.szakdolgozat;

import hu.kovacsa.szakdolgozat.config.AppConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(AppConfig.class)
public class Main {

	public static void main(final String[] args) {
		final ConfigurableApplicationContext ctx = SpringApplication.run(
				Main.class, args);
		ctx.registerShutdownHook();
	}

}