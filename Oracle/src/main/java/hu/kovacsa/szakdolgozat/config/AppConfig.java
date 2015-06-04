package hu.kovacsa.szakdolgozat.config;

import hu.kovacsa.szakdolgozat.service.UserService;
import hu.kovacsa.szakdolgozat.service.impl.OracleUserServiceImpl;

import java.sql.SQLException;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * Created by Aron Kovacs.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "hu.kovacsa.szakdolgozat.repository")
@ComponentScan(basePackages = { "hu.kovacsa.szakdolgozat.service.impl", "hu.kovacsa.szakdolgozat.rest" })
public class AppConfig implements TransactionManagementConfigurer {

	@Value("${datasource.driverclassname}")
	private String driverClassName;

	@Value("${datasource.url}")
	private String url;

	@Value("${datasource.user}")
	private String user;

	@Value("${datasource.password}")
	private String password;

	@Bean
	@DependsOn("dataSource")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setJpaVendorAdapter(jpaVendorAdapter());
		emf.setDataSource(dataSource());
		emf.setPackagesToScan("hu.kovacsa.szakdolgozat.model");
		//emf.setLoadTimeWeaver(loadTimeWeaver());
		return emf;
	}

	//	@Bean
	//	public InstrumentationLoadTimeWeaver loadTimeWeaver() {
	//		return new InstrumentationLoadTimeWeaver();
	//	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		final EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
		jpaVendorAdapter.setShowSql(true);
		return jpaVendorAdapter;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		final JpaTransactionManager tm = new JpaTransactionManager(entityManagerFactory().getObject());
		tm.setDataSource(dataSource());
		tm.setJpaDialect(new EclipseLinkJpaDialect());
		return tm;
	}

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		OracleDataSource dataSource = null;
		try {
			dataSource = new OracleDataSource();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		dataSource.setUser(user);
		dataSource.setPassword(password);
		dataSource.setURL(url);
		return dataSource;
	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return transactionManager();
	}

	@Bean
	public UserService userService() {
		return new OracleUserServiceImpl();
	}
}
