package jpa.sideStudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SideStudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideStudyApplication.class, args);
	}

}
