package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The PizzaPriceScheduler class implements the scheduled task of discounting pizzas during lunchtime
 * and resetting the prices back to their initial values afterwards.
 * This class uses the PizzaService class to access and update pizza prices.
 *
 * @author Kiiashchenko Dmytro
 */
@Slf4j
@Component
public class PizzaPriceScheduler {

    /**
     * This field contains the discount value. The discount value is a constant.
     */
    private final Double LUNCH_TIME_DISCOUNT = 2.0;

    /**
     * This field contains a flag indicating whether the program was first run.
     * In order that regardless of the server start-up time,
     * the first method that reduces the cost is executed.
     */
    private boolean firstLaunch = true;
    /**
     * This field is used to call methods of PizzaSersice class to work with a database.
     */
    private final PizzaService pizzaService;

    /**
     * This is constructor of PizzaPriceScheduler class.
     */
    public PizzaPriceScheduler(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    /**
     * This method is used to discount the prices of pizzas at 13 o`clock, Monday to Friday.
     * It gets the list of all pizzas from the PizzaService and updates the prices by reducing a constant.
     * If the base price is less than the discount value, the base price is not changed.
     * Finally, it updates the database via PizzaService and logs that all pizzas have changed prices.
     */
    //Выполнение каждую минуту в 00 сек с Пн по Пт (для теста)
//    @Scheduled(cron = "0 * * * * MON-FRI", zone = "Europe/Berlin")
    @Scheduled(cron = "0 0 13 * * MON-FRI", zone = "Europe/Berlin") //Выполнение в 13:00 каждый день с Пн по Пт
    public void schedualePizzaPriceLunch() {
        firstLaunch = false;
        List<Pizza> pizzaList = pizzaService.findAll();
        Double pizzaCurrentBasePrice;
        for (Pizza pizza : pizzaList) {
            pizzaCurrentBasePrice = pizza.getBasePrice();
            //Если текущая цена выше, чем скидка, то вносим изменения, чтобы не было ошибки
            if (pizzaCurrentBasePrice > LUNCH_TIME_DISCOUNT) {
                //Уменьшение стоимости на фиксированную величину
                pizzaCurrentBasePrice -= LUNCH_TIME_DISCOUNT;
                pizza.setBasePrice(pizzaCurrentBasePrice);
                //Сохранение нового значения в БД
                pizzaService.updatePizzaById(pizza.getId(), pizza);
            }
        }
        log.info("It`s lunchtime. The price changed for Pizza.");
    }

    /**
     * This method is used to set up the prices of pizzas after 14 o`clock, Monday to Friday.
     * First, it is checked whether it was the first time the program was run
     * so as not to increase the price above the basic one.
     * It gets the list of all pizzas from the PizzaService and sets the prices back to their initial values.
     * Finally, it updates the database via PizzaService and logs that all pizzas have changed prices.
     */
    //Выполнение каждую минуту в 30 сек с Пн по Пт (для теста)
//    @Scheduled(cron = "30 * * * * MON-FRI", zone = "Europe/Berlin")
    @Scheduled(cron = "0 0 14 * * MON-FRI", zone = "Europe/Berlin") //Выполнение в 14:00 каждый день с Пн по Пт
    public void schedualePizzaPriceNoLunch() {
        //Если это первый запуск, то метод не должен увеличивать стоимость
        if (firstLaunch) {
            firstLaunch = false;
            return;
        }
        //Если это не первый запуск, то метод должен увеличить стоимость (т.е. цена была уже снижена)
        List<Pizza> pizzaList = pizzaService.findAll();
        Double pizzaCurrentBasePrice;
        for (Pizza pizza : pizzaList) {
            pizzaCurrentBasePrice = pizza.getBasePrice();
            pizzaCurrentBasePrice += LUNCH_TIME_DISCOUNT;//Увеличение стоимости на фиксированную величину
            pizza.setBasePrice(pizzaCurrentBasePrice);
            //Сохранение нового значения в БД
            pizzaService.updatePizzaById(pizza.getId(), pizza);
        }
        log.info("Lunch is over. Pizza prices are basic.");
    }

    /**
     * This method is used for tests.
     */
    public void setFirstLaunch(boolean firstLaunch) {
        this.firstLaunch = firstLaunch;
    }

}
