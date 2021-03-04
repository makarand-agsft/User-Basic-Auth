package com.formz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class })
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableSwagger2
@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
@EnableAsync
public class FormzApplication {

	private static final Logger log = LogManager.getLogger(FormzApplication.class);

	public static void main(String[] args) {
		log.info("STARTING APPLICATION");
		SpringApplication.run(FormzApplication.class, args);
		log.info("APPLICATION STARTED");
	}

}
