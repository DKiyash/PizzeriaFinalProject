package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.exeptions.DuplicateEntryException;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.exeptions.PizzeriaNotFoundException;
import de.telran.pizzeriaproject.services.PizzeriaSersice;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


class PizzeriaControllerUnitTest {

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
    class CreateNewPizzeriaUnitTest {
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

    //Тестирование метода getPizzeriaById (Получение пиццерии по ID)
    @Nested
    @DisplayName("Получение пиццерии по id")
    class GetPizzeriaByIdUnitTest {
        @Test
        @DisplayName("Успешное получение пиццерии по id, код 200")
        void getPizzeriaById_returnPizzeriaAndStatus200() {
            Long id = 2L;
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.findById(id)).thenReturn(Optional.of(newPizzeria));

            ResponseEntity<?> response = pizzeriaController.getPizzeriaById(id);
            Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Код ответа должен быть 200");
            Assertions.assertEquals(newPizzeria, response.getBody());
        }

        @Test
        @DisplayName("Pizzeria is not found for id:, код 404")
        void getPizzeria_returnStatus404() {
            Long id = 2L;
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.findById(id)).thenReturn(Optional.empty());

            ResponseEntity<?> response = pizzeriaController.getPizzeriaById(id);
            Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "Код ответа должен быть 404");
            Assert.isTrue(response.getBody() == null, "Тело ответа должно быть пустым");
        }
    }

    //Тестирование метода updatePizzeriaById (Обновление пиццерии)
    @Nested
    @DisplayName("Обновление пиццерии")
    class UpdatePizzeriaUnitTest {
        @Test
        @DisplayName("Успешное обновление пиццерии по id=2, код 200")
        void updatePizzeriaById_returnPizzeriaAndStatus200() {
            Long id = 2L;
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.updatePizzeriaById(id, newPizzeria)).thenReturn(newPizzeria);

            ResponseEntity<?> response = pizzeriaController.updatePizzeriaById(id, newPizzeria);
            Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Код ответа должен быть 200");
            Assertions.assertEquals(newPizzeria, response.getBody());
        }

        @Test
        @DisplayName("Pizzeria is not found for id:, код 404")
        void updatePizzeria_returnStatus404() {
            Long id = 2L;
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.updatePizzeriaById(id, newPizzeria)).thenThrow(new PizzeriaNotFoundException("Pizzeria is not found for id:"));

            ResponseEntity<?> response = pizzeriaController.updatePizzeriaById(id, newPizzeria);
            Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "Код ответа должен быть 404");
            Assert.isTrue(response.getBody() == "Pizzeria is not found for id:", "Должно быть объяснение ошибки");
        }

        @Test
        @DisplayName("Pizza list is not correct, код 400")
        void updatePizzeria_returnStatus400() {
            Long id = 2L;
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.updatePizzeriaById(id, newPizzeria)).thenThrow(new PizzaNotFoundException("Pizza is not found for id:"));

            ResponseEntity<?> response = pizzeriaController.updatePizzeriaById(id, newPizzeria);
            Assert.isTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST, "Код ответа должен быть 400");
            Assert.isTrue(response.getBody() == "Pizza is not found for id:", "Должно быть объяснение ошибки");
        }

        @Test
        @DisplayName("Pizzeria with these parameters already exists, код 409")
        void updatePizzeria_returnStatus409() {
            Long id = 2L;
            //Создаем новый список пицц для теста
            Set<Pizza> pizzaSet1 = new HashSet<>();
            pizzaSet1.add(new Pizza("Pizza_name_01", "Description_01", 10.0, "url_01"));
            pizzaSet1.add(new Pizza("Pizza_name_02", "Description_02", 10.0, "url_02"));
            //Создаем новый объект пиццерия для теста
            Pizzeria newPizzeria = new Pizzeria(3L, "Pizzeria_03", "Address_03", pizzaSet1);

            Mockito.when(pizzeriaSersice.updatePizzeriaById(id, newPizzeria)).thenThrow(new DataIntegrityViolationException("Pizzeria with these parameters already exists"));

            ResponseEntity<?> response = pizzeriaController.updatePizzeriaById(id, newPizzeria);
            Assert.isTrue(response.getStatusCode() == HttpStatus.CONFLICT, "Код ответа должен быть 409");
            Assert.isTrue(response.getBody() == "Pizzeria with these parameters already exists", "Должно быть объяснение ошибки");
        }
    }

    //Тестирование метода deletePizzeriaById (Удаление пиццерии по ID)
    @Nested
    @DisplayName("Удаление пиццерии по id")
    class DeletePizzeriaByIdUnitTest {
        @Test
        @DisplayName("Успешное удаление пиццерии по id, код 200")
        void deletePizzeriaById_returnStatus200() {
            Long id = 2L;

            ResponseEntity<?> response = pizzeriaController.deletePizzeriaById(id);
            Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Код ответа должен быть 200");
            Assert.isTrue(response.getBody() == null, "Тело ответа должно быть пустым");
        }

        @Test
        @DisplayName("Pizzeria is not found for id:, код 404")
        void deletePizzeria_returnStatus404() {
//            Long id = 2L;
//
////            Mockito.doThrow(new PizzeriaNotFoundException("Pizzeria is not found for id:")).when(pizzeriaController.deletePizzeriaById(id));
//            Mockito.doThrow(new PizzeriaNotFoundException("Pizzeria is not found for id:")).when(Mockito.mock(PizzeriaSersice.class)).deleteById(id);
//
//            ResponseEntity<?> response = pizzeriaController.deletePizzeriaById(id);
//            System.out.println(response);
//            Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "Код ответа должен быть 404");
//            Assert.isTrue(response.getBody() == null, "Тело ответа должно быть пустым");
        }
    }

}