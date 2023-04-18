package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

class PizzaPriceSchedulerUnitTest {

    PizzaService pizzaService = Mockito.mock(PizzaService.class);

    PizzaPriceScheduler pizzaPriceScheduler = new PizzaPriceScheduler(pizzaService);

    //Тестирование метода schedualePizzaPriceLunch (снижение стоимости всех пицц в обед)
    @Test
    @DisplayName("Снижение стоимости всех пицц в обед")
    void schedualePizzaPriceLunchUnitTest() {
        //Создаем список пицц для теста
        List<Pizza> pizzaList = new ArrayList<>();
        Pizza pizza1 = new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01");
        pizza1.setP_id(1L);
        pizzaList.add(pizza1);

        Pizza pizza2 = new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02");
        pizza2.setP_id(2L);
        pizzaList.add(pizza2);

        Mockito.when(pizzaService.findAll()).thenReturn(pizzaList);

        pizzaPriceScheduler.schedualePizzaPriceLunch();
        Assert.isTrue(pizzaList.get(0).getP_base_price() == 8.0, "Цена для пицы с id=1 должна быть снижена в обед");
        Assert.isTrue(pizzaList.get(1).getP_base_price() == 8.0, "Цена для пицы с id=2 должна быть снижена в обед");
    }

    //Тестирование метода schedualePizzaPriceNoLunch (снижение стоимости всех пицц в обед)
    @Test
    @DisplayName("Возврат базовой цены после обеденной скидки")
    void schedualePizzaPriceNoLunchUnitTest() {
        pizzaPriceScheduler.setFirstLaunch(false);
        //Создаем список пицц для теста
        List<Pizza> pizzaList = new ArrayList<>();
        Pizza pizza1 = new Pizza("Pizza_name_01", "Description_01", 8.0, "url_01");
        pizza1.setP_id(1L);
        pizzaList.add(pizza1);

        Pizza pizza2 = new Pizza("Pizza_name_02", "Description_02", 8.0, "url_02");
        pizza2.setP_id(2L);
        pizzaList.add(pizza2);

        Mockito.when(pizzaService.findAll()).thenReturn(pizzaList);

        pizzaPriceScheduler.schedualePizzaPriceNoLunch();
        Assert.isTrue(pizzaList.get(0).getP_base_price() == 10.0, "Цена для пицы с id=1 должна быть снижена в обед");
        Assert.isTrue(pizzaList.get(1).getP_base_price() == 10.0, "Цена для пицы с id=2 должна быть снижена в обед");
    }
}