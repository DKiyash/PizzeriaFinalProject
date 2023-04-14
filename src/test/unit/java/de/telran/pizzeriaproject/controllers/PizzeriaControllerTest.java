package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.services.PizzeriaSersice;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PizzeriaControllerTest {

    PizzeriaSersice pizzeriaSersice = Mockito.mock(PizzeriaSersice.class);
    PizzeriaController pizzeriaController = new PizzeriaController(pizzeriaSersice);

    //Тестирование метода getAllPizzerias (Получение списка всех пиццерий)
    @Test
    @DisplayName("Успешное получение всех пиццерий")
    void getAllPizzerias() {
        Set<Pizza> pizzaSet1 = new HashSet<>();
        pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
        pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
        List<Pizzeria> pizzeriaList = List.of(
                new Pizzeria(1L, "Pizzeria_01", "Address_01", pizzaSet1),
                new Pizzeria(2L, "Pizzeria_02", "Address_02", pizzaSet1)
        );
        Pageable pageable = PageRequest.of(0, 5);

        Mockito.when(pizzeriaSersice.findAll(pageable)).thenReturn(pizzeriaList);

        ResponseEntity<List<Pizzeria>> response = pizzeriaController.getAllPizzerias(pageable);

        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Код ответа должен быть 200");
        Assert.notNull(response.getBody(), "Ответ должен содержать список объектов Pizzeria");
    }

    @Test
    void createPizzeria() {
    }

    @Test
    void getPizzeriaById() {
    }

    @Test
    void updatePizzeriaById() {
    }

    @Test
    void deletePizzeriaById() {
    }
}