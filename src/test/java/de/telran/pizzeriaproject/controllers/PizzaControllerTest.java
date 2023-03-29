package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.services.PizzaSersice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
//Чтобы появилась возможность внедрить в тестовый класс бин MockMvc
@AutoConfigureMockMvc
//Создаем только бин контроллера
@WebMvcTest(controllers = PizzaController.class)
class PizzaControllerTest {

    static final String API_PATH = "/api/v1/pizzas";

    //Предназначен для тестирования контроллеров,
    //позволяет тестировать контроллеры без запуска http-сервера
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PizzaSersice pizzaSersice;

    //Тестирование метода getAllPizzas
    @Test
    //Чтобы избежать: Error message = Unauthorized
    @WithMockUser
    void getAllPizzas_returnAllPizzasAndStatus200() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<Pizza> pizzas = Arrays.asList(
                new Pizza("Pizza name_01", "Description_01", 10.00, "url_01"),
                new Pizza("Pizza name_02", "Description_02", 10.00, "url_02"));
        Page<Pizza> pizzaPage = new PageImpl<>(pizzas, pageable, pizzas.size());

        Mockito.when(pizzaSersice.findAll(pageable)).thenReturn(pizzaPage);

        mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    //Чтобы избежать: Error message = Unauthorized
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void createPizza_returnIdCreatedPizzaAndLocationAndStatus201() {
//        Pizza newPizza = new Pizza("Pizza name_01","Description_01", 10.00, "url_01");

    }

    //Получение пиццы по ID
    @Nested
    class GetByIdTest {
        @DisplayName("getPizzaById found and return Pizza and Response 200 (OK)")
        @Test
        @WithMockUser
        void getPizzaById_returnPizzaAndStatus200() throws Exception {
            Pizza newPizza = new Pizza();
            newPizza.setP_id(1L);
            newPizza.setP_name("Pizza name_01");
            newPizza.setP_description("Description_01");
            newPizza.setP_base_price(10.00);
            newPizza.setP_photo_link("url_01");

            Mockito.when(pizzaSersice.findById(1L)).thenReturn(Optional.of(newPizza));

            mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.p_id").value("1"))
                    .andExpect(jsonPath("$.p_name").value("Pizza name_01"))
                    .andExpect(jsonPath("$.p_description").value("Description_01"))
                    .andExpect(jsonPath("$.p_base_price").value("10.0"))
                    .andExpect(jsonPath("$.p_photo_link").value("url_01"))
                    .andDo(MockMvcResultHandlers.print());

            //Проверка, что метод вызывался только 1 раз
            Mockito.verify(pizzaSersice, Mockito.times(1)).findById(1L);
        }

        @DisplayName("getPizzaById notfound Pizza and return 404 (NOT_FOUND)")
        @Test
        @WithMockUser
        public void getEventByIdTest_ShouldReturnNotFound() throws Exception {
            Mockito.when(pizzaSersice.findById(1L)).thenReturn(Optional.empty());
            mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + "/1"))
                    .andExpect(status().isNotFound())
                    .andDo(MockMvcResultHandlers.print());
            //Проверка, что метод вызывался только 1 раз
            Mockito.verify(pizzaSersice, Mockito.times(1)).findById(1L);
        }
    }

    @Test
    void updatePizzaById() {
    }

    @Test
    void deletePizzaById() {
    }
}

/*
@RunWith(MockitoJUnitRunner.class)
public class PizzaControllerTest {

    @InjectMocks
    private PizzaController pizzaController;

    @Mock
    private PizzaService pizzaService;

    @Test
    public void testGetAllPizzas() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Pizza> pizzas = Arrays.asList(new Pizza(), new Pizza(), new Pizza());
        Page<Pizza> pizzaPage = new PageImpl<>(pizzas, pageable, pizzas.size());

        when(pizzaService.findAll(pageable)).thenReturn(pizzaPage);

        ResponseEntity<?> responseEntity = pizzaController.getAllPizzas(pageable);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(pizzas, responseEntity.getBody());
    }

    @Test
    public void testCreatePizza() throws URISyntaxException {
        Pizza newPizza = new Pizza();
        newPizza.setP_id(1L);

        when(pizzaService.save(newPizza)).thenReturn(newPizza);

        ResponseEntity<?> responseEntity = pizzaController.createPizza(newPizza);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getHeaders().getLocation());
        assertEquals(newPizza.getP_id(), responseEntity.getBody());
    }

    @Test
    public void testCreatePizza_Failure() {
        Pizza newPizza = new Pizza();
        newPizza.setP_id(1L);

        when(pizzaService.save(newPizza)).thenReturn(null);

        ResponseEntity<?> responseEntity = pizzaController.createPizza(newPizza);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void testGetPizzaById() {
        Long id = 1L;
        Pizza pizza = new Pizza();
        pizza.setP_id(id);

        when(pizzaService.findById(id)).thenReturn(Optional.of(pizza));

        ResponseEntity<?> responseEntity = pizzaController.getPizzaById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(pizza, responseEntity.getBody());
    }

    @Test
    public void testGetPizzaById_Failure() {
        Long id = 1L;

        when(pizzaService.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = pizzaController.getPizzaById(id);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdatePizzaById() {
        Long id = 1L;
        Pizza newPizza = new Pizza();

        when(pizzaService.updatePizzaById(id, newPizza)).thenReturn(newPizza);

        ResponseEntity<?> responseEntity = pizzaController.updatePizzaById(id, newPizza);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(newPizza, responseEntity.getBody());
    }

    @Test
    public void testUpdatePizzaById_Failure() {
        Long id = 1L;
        Pizza newPizza = new Pizza();

        when(pizzaService.updatePizzaById(id, newPizza)).thenThrow(new PizzaNotFoundException());

        ResponseEntity<?> responseEntity = pizzaController.updatePizzaById(id, newPizza);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void testDeletePizzaById() {
        Long id = 1L;

        doNothing().when(pizzaService).deleteById(id);

        ResponseEntity<?> responseEntity = pizzaController.deletePizzaById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testDeletePizzaById_Failure() {
        Long id = 1L;

        doThrow(new PizzaNotFoundException()).when(pizzaService).deleteById(id);

        ResponseEntity<?> responseEntity = pizzaController.deletePizzaById(id);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}

 */