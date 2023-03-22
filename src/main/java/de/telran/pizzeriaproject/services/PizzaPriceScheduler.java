package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class PizzaPriceScheduler {

    private final Double LANCH_DISKONT = 2.0;
    private final PizzaSersice pizzaSersice;

    public PizzaPriceScheduler(PizzaSersice pizzaSersice) {
        this.pizzaSersice = pizzaSersice;
    }

//    @Scheduled(cron = "0 * * * * MON-FRI", zone = "Europe/Berlin") //Выполнение каждую минуту в 00 сек с Пн по Пт (для теста)
    @Scheduled(cron = "0 0 13 * * MON-FRI", zone = "Europe/Berlin") //Выполнение в 13:00 каждый день с Пн по Пт
    public void schedualePizzaPriceLanch(){
        List<Pizza> pizzaList = pizzaSersice.findAll();
        Double pizzaCurrentBasePrice;
        for (Pizza pz: pizzaList) {
            pizzaCurrentBasePrice = pz.getP_base_price();
//            System.out.println("pizzaCurrentBasePrice до обеда = " + pizzaCurrentBasePrice);
            pizzaCurrentBasePrice -= LANCH_DISKONT;//Уменьшение стоимости на фиксированную величину
            pz.setP_base_price(pizzaCurrentBasePrice);
//            System.out.println("pizzaCurrentBasePrice в обед = " + pz.getP_base_price());
        }
    }

//    @Scheduled(cron = "30 * * * * MON-FRI", zone = "Europe/Berlin") //Выполнение каждую минуту в 30 сек с Пн по Пт (для теста)
    @Scheduled(cron = "0 0 14 * * MON-FRI", zone = "Europe/Berlin") //Выполнение в 14:00 каждый день с Пн по Пт
    public void schedualePizzaPriceNoLanch(){
        List<Pizza> pizzaList = pizzaSersice.findAll();
        Double pizzaCurrentBasePrice;
        for (Pizza pz: pizzaList) {
            pizzaCurrentBasePrice = pz.getP_base_price();
//            System.out.println("pizzaCurrentBasePrice в обед = " + pizzaCurrentBasePrice);
            pizzaCurrentBasePrice += LANCH_DISKONT;//Увеличение стоимости на фиксированную величину
            pz.setP_base_price(pizzaCurrentBasePrice);
//            System.out.println("pizzaCurrentBasePrice после обеда = " + pz.getP_base_price());
        }
    }

}
