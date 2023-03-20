package de.telran.pizzeriaproject.bootstrap;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.repositories.PizzaRepositories;
import de.telran.pizzeriaproject.repositories.PizzeriaRepositories;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PizzaRepositories pizzaRepositories;
    private final PizzeriaRepositories pizzeriaRepositories;

    public DataInitializer(PizzaRepositories pizzaRepositories, PizzeriaRepositories pizzeriaRepositories) {
        this.pizzaRepositories = pizzaRepositories;
        this.pizzeriaRepositories = pizzeriaRepositories;
    }


    @Override
    public void run(String... args) throws Exception {
//        Заполнение таблицы pizzeria (для теста)
        Pizzeria pizzeria1 = new Pizzeria("Pizzeria_01", "Address_01");
        Pizzeria pizzeria2 = new Pizzeria("Pizzeria_02", "Address_02");
        Pizzeria pizzeria3 = new Pizzeria("Pizzeria_03", "Address_03");
        pizzeriaRepositories.save(pizzeria1);
        pizzeriaRepositories.save(pizzeria2);
        pizzeriaRepositories.save(pizzeria3);

//        Заполнение таблицы pizza
        Pizza pizza1 = new Pizza("Pizza name_01", "Description_01", 10.00, "url_01");
        Pizza pizza2 = new Pizza("Pizza name_02", "Description_02", 11.00, "url_02");
        Pizza pizza3 = new Pizza("Pizza name_03", "Description_03", 9.00, "url_03");
        pizzaRepositories.save(pizza1);
        pizzaRepositories.save(pizza2);
        pizzaRepositories.save(pizza3);

    }

}

//        Pizzeria pizzeria1 = new Pizzeria("Pizzeria Berlin Steglitz", "Rheinstrasse 23, 12161 Berlin, Germany");
//        Pizzeria pizzeria2 = new Pizzeria("Pizzeria Berlin Wedding", "Gerichtstrasse 41, 13347 Berlin, Germany");
//        Pizzeria pizzeria3 = new Pizzeria("Pizzeria Berlin Mitte", "Schumannstrasse 19, 10117 Berlin, Germany");
//        Pizza pizza1 = new Pizza("Pizza Manhattan", "Peperoni, Mozarella, sauce, Mushrooms", 10.00, "url Pizza Manhattan");
//        Pizza pizza2 = new Pizza("Pizza Margarita", "Mozarella(x2), sauce', 11, 'url Pizza Margarita", 11.00, "url Pizza Margarita");
//        Pizza pizza3 = new Pizza("Pizza Pepperoni With Tomatoes", "Tomatoes, Peperoni, BBQ sauce, Mozarella", 9.00, "url Pizza Pepperoni With Tomatoes");
