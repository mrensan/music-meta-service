package net.ensan.musify;

import net.ensan.musify.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class MusifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusifyApplication.class, args);
	}

	@Autowired
	private CrawlerService crawlerService;

	@Profile("!test")
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext context) {
		return args ->
			crawlerService.crawl();
	}
}
