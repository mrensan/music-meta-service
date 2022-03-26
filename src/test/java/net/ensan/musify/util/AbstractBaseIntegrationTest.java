package net.ensan.musify.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public class AbstractBaseIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBaseIntegrationTest.class);

    @Container
    protected static final MySQLContainer<?> DATABASE = new MySQLContainer<>("mysql:5.7")
        .withExposedPorts(3306)
        .withDatabaseName("music_meta_service")
        .withUsername("admin")
        .withPassword("admin")
        .withCommand("mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
        .withLogConsumer(new Slf4jLogConsumer(LOGGER));

    @DynamicPropertySource
    static void databaseProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE::getUsername);
        registry.add("spring.datasource.password", DATABASE::getPassword);

        registry.add("spring.liquibase.url", DATABASE::getJdbcUrl);
        registry.add("spring.liquibase.user", DATABASE::getUsername);
        registry.add("spring.liquibase.password", DATABASE::getPassword);
        registry.add("spring.liquibase.enabled", () -> true);

        registry.add("spring.jpa.show-sql", () -> true);
    }
}
