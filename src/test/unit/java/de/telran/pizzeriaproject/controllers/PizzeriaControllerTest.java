package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.exeptions.DuplicateEntryException;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.services.PizzeriaSersice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


class PizzeriaControllerTest {

    PizzeriaSersice pizzeriaSersice = Mockito.mock(PizzeriaSersice.class);
    PizzeriaController pizzeriaController = new PizzeriaController(pizzeriaSersice);

    //Тестирование метода getAllPizzerias (Получение списка всех пиццерий)
    @Test
    @DisplayName("Успешное получение всех пиццерий, код 200")
    void getAllPizzerias_returnAllPizzeriasAndStatus200() {
        //Создаем новый список пицц для теста
        Set<Pizza> pizzaSet1 = new HashSet<>();
        pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
        pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
        //Создаем новый список пиццерий для теста
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

    //Тестирование метода createPizzeria (Создание новой пиццерии)
    @Nested
    @DisplayName("Создание новой пиццерии")
    class CreateNewPizzeriaTest {
        @Test
        @DisplayName("Успешное создание новой пиццерии, код 201")
        void createPizzeria_returnIdCreatedPizzeriaAndLocationAndStatus201() {
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            // create a mock request (for location)
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            Mockito.when(pizzeriaSersice.save(newPizzeria)).thenReturn(newPizzeria);

            ResponseEntity<?> response = pizzeriaController.createPizzeria(newPizzeria);
            Assert.isTrue(response.getStatusCode() == HttpStatus.CREATED, "Код ответа должен быть 201");
            Assert.notNull(response.getHeaders().getLocation(), "Ответ должен содержать список location");
            Assertions.assertEquals(newPizzeria.getPr_id(), response.getBody());
        }

        @Test
        @DisplayName("Internal Server Error, код 500")
        void createPizzeria_returnStatus500() {
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.save(newPizzeria)).thenReturn(null);

            ResponseEntity<?> response = pizzeriaController.createPizzeria(newPizzeria);
            Assert.isTrue(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR, "Код ответа должен быть 500");
            Assert.isNull(response.getBody(), "Тело ответ должно быть пустым");
        }

        @Test
        @DisplayName("Pizza list is not correct, код 400")
        void createPizzeria_returnStatus400() {
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.save(newPizzeria)).thenThrow(new PizzaNotFoundException("Pizza is not found for id:"));

            ResponseEntity<?> response = pizzeriaController.createPizzeria(newPizzeria);
            Assert.isTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST, "Код ответа должен быть 400");
            Assert.isTrue(response.getBody() == "Pizza is not found for id:", "Должно быть объяснение ошибки");
        }
        @Test
        @DisplayName("Pizzeria with these parameters already exists, код 409")
        void createPizzeria_returnStatus409() {
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.save(newPizzeria)).thenThrow(new DuplicateEntryException("Pizzeria with these parameters already exists"));

            ResponseEntity<?> response = pizzeriaController.createPizzeria(newPizzeria);
            Assert.isTrue(response.getStatusCode() == HttpStatus.CONFLICT, "Код ответа должен быть 409");
            Assert.isTrue(response.getBody() == "Pizzeria with these parameters already exists", "Должно быть объяснение ошибки");
        }
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