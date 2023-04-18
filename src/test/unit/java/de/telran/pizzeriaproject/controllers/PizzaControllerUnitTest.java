package de.telran.pizzeriaproject.controllers;


import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.exeptions.DuplicateEntryException;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.services.PizzaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

class PizzaControllerUnitTest {

    PizzaService pizzaService = Mockito.mock(PizzaService.class);
    PizzaController pizzaController = new PizzaController(pizzaService);

    //Тестирование метода getAllPizzas (Получение списка всех пицц)
    @Test
    @DisplayName("Успешное получение всех пиццерий, код 200")
    void getAllPizzas_returnAllPizzasAndStatus200() {
        //Создаем новый список пицц для теста
        Pageable pageable = PageRequest.of(0, 5);
        List<Pizza> pizzaList = new ArrayList<>();
        Pizza pizza1 = new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01");
        pizza1.setP_id(1L);
        pizzaList.add(pizza1);

        Pizza pizza2 = new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02");
        pizza2.setP_id(2L);
        pizzaList.add(pizza2);

        Mockito.when(pizzaService.findAll(pageable)).thenReturn(pizzaList);

        ResponseEntity<List<Pizza>> response = pizzaController.getAllPizzas(pageable);
        Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Код ответа должен быть 200");
        Assert.notNull(response.getBody(), "Ответ должен содержать список объектов Pizzeria");
    }

    //Тестирование метода createPizza (Создание новой пиццы)
    @Nested
    @DisplayName("Создание новой пиццы")
    class CreateNewPizzaUnitTest {
        @Test
        @DisplayName("Успешное создание новой пиццы, код 201")
        void createPizza_returnIdCreatedPizzaAndLocationAndStatus201() {
            //Создаем новый объект пицца для теста
            Pizza newPizza = new Pizza("Pizza_name_03", "Description_03", 10.0, "url_03");
            newPizza.setP_id(3L);

            // create a mock request (for location)
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            Mockito.when(pizzaService.save(newPizza)).thenReturn(newPizza);

            ResponseEntity<?> response = pizzaController.createPizza(newPizza);
            Assert.isTrue(response.getStatusCode() == HttpStatus.CREATED, "Код ответа должен быть 201");
            Assert.notNull(response.getHeaders().getLocation(), "Ответ должен содержать список location");
            Assertions.assertEquals(newPizza.getP_id(), response.getBody());
        }

        @Test
        @DisplayName("Internal Server Error, код 500")
        void createPizza_returnStatus500() {
            //Создаем новый объект пицца для теста
            Pizza newPizza = new Pizza("Pizza_name_03", "Description_03", 10.0, "url_03");
            newPizza.setP_id(3L);

            Mockito.when(pizzaService.save(newPizza)).thenReturn(null);

            ResponseEntity<?> response = pizzaController.createPizza(newPizza);
            Assert.isTrue(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR, "Код ответа должен быть 500");
            Assert.isNull(response.getBody(), "Тело ответ должно быть пустым");
        }

        @Test
        @DisplayName("Pizza with these parameters already exists, код 409")
        void createPizzeria_returnStatus409() {
            //Создаем новый объект пицца для теста
            Pizza newPizza = new Pizza("Pizza_name_03", "Description_03", 10.0, "url_03");
            newPizza.setP_id(3L);

            Mockito.when(pizzaService.save(newPizza)).thenThrow(new DuplicateEntryException("Pizza with these parameters already exists"));

            ResponseEntity<?> response = pizzaController.createPizza(newPizza);
            Assert.isTrue(response.getStatusCode() == HttpStatus.CONFLICT, "Код ответа должен быть 409");
            Assert.isTrue(response.getBody() == "Pizza with these parameters already exists", "Должно быть объяснение ошибки");
        }
    }

    //Тестирование метода getPizzaById (Получение пиццы по ID)
    @Nested
    @DisplayName("Получение пиццы по id")
    class GetPizzaByIdUnitTest {
        @Test
        @DisplayName("Успешное получение пиццы по id, код 200")
        void getPizzaById_returnPizzaAndStatus200() {
            Long id = 3L;
            //Создаем новый объект пицца для теста
            Pizza newPizza = new Pizza("Pizza_name_03", "Description_03", 10.0, "url_03");
            newPizza.setP_id(id);

            Mockito.when(pizzaService.findById(id)).thenReturn(Optional.of(newPizza));

            ResponseEntity<?> response = pizzaController.getPizzaById(id);
            Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Код ответа должен быть 200");
            Assertions.assertEquals(newPizza, response.getBody());
        }

        @Test
        @DisplayName("Pizza is not found for id:, код 404")
        void getPizzeria_returnStatus404() {
            Long id = 3L;
            //Создаем новый объект пицца для теста
            Pizza newPizza = new Pizza("Pizza_name_03", "Description_03", 10.0, "url_03");
            newPizza.setP_id(id);

            Mockito.when(pizzaService.findById(id)).thenReturn(Optional.empty());

            ResponseEntity<?> response = pizzaController.getPizzaById(id);
            Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "Код ответа должен быть 404");
            Assert.isTrue(response.getBody() == null, "Тело ответа должно быть пустым");
        }
    }

    //Тестирование метода updatePizzaById (Обновление пиццы)
    @Nested
    @DisplayName("Обновление пиццы")
    class UpdatePizzaUnitTest {
        @Test
        @DisplayName("Успешное обновление пиццы по id=2, код 200")
        void updatePizzaById_returnPizzaAndStatus200() {
            Long id = 3L;
            //Создаем новый объект пицца для теста
            Pizza newPizza = new Pizza("Pizza_name_03", "Description_03", 10.0, "url_03");
            newPizza.setP_id(id);

            Mockito.when(pizzaService.updatePizzaById(id, newPizza)).thenReturn(newPizza);

            ResponseEntity<?> response = pizzaController.updatePizzaById(id, newPizza);
            Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Код ответа должен быть 200");
            Assertions.assertEquals(newPizza, response.getBody());
        }

        @Test
        @DisplayName("Pizza is not found for id:, код 404")
        void updatePizza_returnStatus404() {
            Long id = 3L;
            //Создаем новый объект пицца для теста
            Pizza newPizza = new Pizza("Pizza_name_03", "Description_03", 10.0, "url_03");
            newPizza.setP_id(id);

            Mockito.when(pizzaService.updatePizzaById(id, newPizza)).thenThrow(new PizzaNotFoundException("Pizza is not found for id:"));

            ResponseEntity<?> response = pizzaController.updatePizzaById(id, newPizza);
            Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "Код ответа должен быть 404");
            Assert.isTrue(response.getBody() == "Pizza is not found for id:", "Должно быть объяснение ошибки");
        }

        @Test
        @DisplayName("Pizza with these parameters already exists, код 409")
        void updatePizza_returnStatus409() {
            Long id = 3L;
            //Создаем новый объект пицца для теста
            Pizza newPizza = new Pizza("Pizza_name_03", "Description_03", 10.0, "url_03");
            newPizza.setP_id(id);

            Mockito.when(pizzaService.updatePizzaById(id, newPizza)).thenThrow(new DataIntegrityViolationException("Pizza with these parameters already exists"));

            ResponseEntity<?> response = pizzaController.updatePizzaById(id, newPizza);
            Assert.isTrue(response.getStatusCode() == HttpStatus.CONFLICT, "Код ответа должен быть 409");
            Assert.isTrue(response.getBody() == "Pizza with these parameters already exists", "Должно быть объяснение ошибки");
        }
    }

    //Тестирование метода deletePizzeriaById (Удаление пиццерии по ID)
    @Nested
    @DisplayName("Удаление пиццы по id")
    class DeletePizzaByIdUnitTest {
        @Test
        @DisplayName("Успешное удаление пиццы по id, код 200")
        void deletePizzaById_returnStatus200() {
            Long id = 2L;

            ResponseEntity<?> response = pizzaController.deletePizzaById(id);
            Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Код ответа должен быть 200");
            Assert.isTrue(response.getBody() == null, "Тело ответа должно быть пустым");
        }

        @Test
        @DisplayName("Pizza is not found for id:, код 404")
        void deletePizza_returnStatus404() {
            Long id = 2L;

            Mockito.doThrow(new PizzaNotFoundException("Pizza is not found for id:")).when(pizzaService).deleteById(id);

            ResponseEntity<?> response = pizzaController.deletePizzaById(id);
            System.out.println(response);
            Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "Код ответа должен быть 404");
            Assert.isTrue(response.getBody() == null, "Тело ответа должно быть пустым");
        }
    }
}
