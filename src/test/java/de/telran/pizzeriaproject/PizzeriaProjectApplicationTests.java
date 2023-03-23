package de.telran.pizzeriaproject;

import de.telran.pizzeriaproject.repositories.PizzaRepositories;
import de.telran.pizzeriaproject.repositories.PizzeriaRepositories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PizzeriaProjectApplicationTests {

    @Autowired
    PizzeriaRepositories pizzeriaRepositories;

    @Autowired
    PizzaRepositories pizzaRepositories;
    @Test
    void testPizzeriaRepositories() {
        Long count = pizzeriaRepositories.count();

        assertThat(count).isGreaterThan(0);
    }

    @Test
    void testPizzaRepositories() {
        Long count = pizzaRepositories.count();

        assertThat(count).isGreaterThan(0);
    }

    @Test
    void contextLoads() {
    }

}
