package de.telran.pizzeriaproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public abstract class IntegrationTestsInfrastructureInitializer {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

        /*
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <version>1.17.6</version>
        <scope>test</scope>
    </dependency>

    static class TestPropertyInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
                    new PostgreSQLContainer<>("postgres:15.2-alpine").withInitScript("db.sql");
            POSTGRESQL_CONTAINER.start();

            TestPropertyValues pgValues = TestPropertyValues.of(
                    "main.datasource.url = " + POSTGRESQL_CONTAINER.getJdbcUrl(),
                    "main.datasource.username = " + POSTGRESQL_CONTAINER.getUsername(),
                    "main.datasource.password = " + POSTGRESQL_CONTAINER.getPassword());
            pgValues.applyTo(applicationContext);
        }
    }
   */
}
