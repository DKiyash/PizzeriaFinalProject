package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The PizzaPriceScheduler class implements the scheduled task of discounting pizzas during lunchtime
 * <p>
 * and resetting the prices back to their initial values afterwards.
 * <p>
 * This class uses the PizzaService class to access and update pizza prices.
 *
 * @author Kiiashchenko Dmytro
 */
@Slf4j
@Component
public class PizzaPriceScheduler {

    private final Double LUNCH_TIME_DISCOUNT = 2.0;
    private boolean firstLaunch = true;
    private final PizzaSersice pizzaSersice;

    public PizzaPriceScheduler(PizzaSersice pizzaSersice) {
        this.pizzaSersice = pizzaSersice;
    }

    /**
     * This method is used to discount the prices of pizzas at 13 o`clock, Monday to Friday.
     * It gets the list of all pizzas from the PizzaService and updates the prices by reducing a constant.
     * If base price is null, it sets it to the current price of the pizza.
     * Finally, it logs the price changes for each pizza and updates the database via the PizzaService.
     */
    @Scheduled(cron = "0 * * * * MON-FRI", zone = "Europe/Berlin")
    //Выполнение каждую минуту в 00 сек с Пн по Пт (для теста)
//    @Scheduled(cron = "0 0 13 * * MON-FRI", zone = "Europe/Berlin") //Выполнение в 13:00 каждый день с Пн по Пт
    public void schedualePizzaPriceLanch() {
        List<Pizza> pizzaList = pizzaSersice.findAll();
        Double pizzaCurrentBasePrice;
        for (Pizza pizza : pizzaList) {
            pizzaCurrentBasePrice = pizza.getP_base_price();
            //Если текущая цена выше, чем скидка, то вносим изменения, чтобы не было ошибки
            if (pizzaCurrentBasePrice > LUNCH_TIME_DISCOUNT) {
                //Уменьшение стоимости на фиксированную величину
                pizzaCurrentBasePrice -= LUNCH_TIME_DISCOUNT;
                pizza.setP_base_price(pizzaCurrentBasePrice);
                //Сохранение нового значения в БД
                pizzaSersice.updatePizzaById(pizza.getP_id(), pizza);
            }
        }
        log.info("It`s lunchtime. The price changed for Pizza.");
    }

    /**
     * This method is used to set up the prices of pizzas after 14 o`clock, Monday to Friday.
     * It gets the list of all pizzas from the PizzaService and sets the prices back to their initial values.
     * If base price is null, it sets it to the current price of the pizza.
     * Finally, it logs the price resets for each pizza and updates the database via the PizzaService.
     */
    @Scheduled(cron = "30 * * * * MON-FRI", zone = "Europe/Berlin")
    //Выполнение каждую минуту в 30 сек с Пн по Пт (для теста)
//    @Scheduled(cron = "0 0 14 * * MON-FRI", zone = "Europe/Berlin") //Выполнение в 14:00 каждый день с Пн по Пт
    public void schedualePizzaPriceNoLanch() {
        //Если это первый запуск, то метод не должен увеличивать стоимость
        if (firstLaunch) {
            firstLaunch = false;
            return;
        }
        //Если это не первый запуск, то метод должен увеличить стоимость (т.е. цена была уже снижена)
        List<Pizza> pizzaList = pizzaSersice.findAll();
        Double pizzaCurrentBasePrice;
        for (Pizza pizza : pizzaList) {
            pizzaCurrentBasePrice = pizza.getP_base_price();
            pizzaCurrentBasePrice += LUNCH_TIME_DISCOUNT;//Увеличение стоимости на фиксированную величину
            pizza.setP_base_price(pizzaCurrentBasePrice);
            //Сохранение нового значения в БД
            pizzaSersice.updatePizzaById(pizza.getP_id(), pizza);
        }
        log.info("Lunch is over. Pizza prices are basic.");
    }

}
