package de.telran.pizzeriaproject;

import de.telran.pizzeriaproject.domain.Pizza;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//Интеграционный тест для сущности Пицца (нужно каждый раз удалять и создавать заново БД pizzeria_project в MySQL WorkBench)
@SpringBootTest
//@Sql({
//        "classpath:sql/Create_test_shcema.sql"
////        "classpath:sql/Create_test_shcema.sql",
////        "classpath:sql/Test_data.sql"
//})
public class PizzaIntegrationTest extends IntegrationTestsInfrastructureInitializer{

    static final String API_PATH = "/api/v1/pizzas";

    //Тестирование метода getAllPizzas (Получение списка всех пицц)
    @Test
    @DisplayName("Успешное получение всех пицц")
    void getAllPizzas_returnAllPizzasAndStatus200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + ""))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    //Тестирование метода createPizza (Создание новой пиццы)
    @Test
    @DisplayName("Успешное создание новой пиццы")
    void createPizza_returnIdCreatedPizzaAndLocationAndStatus201() throws Exception {
        //Создание случайной строки, т.к. некоторые поля должны быть unique иначе ошибка
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        //Создаем новый объект пицца (которого нет в базе)
        Pizza newPizza = new Pizza();
        newPizza.setP_id(Mockito.any());
        newPizza.setP_name("Pizza_name_" + generatedString);
        newPizza.setP_description("Description_" + generatedString);
        newPizza.setP_base_price(10.0);
        newPizza.setP_photo_link("url");

        MvcResult result=mockMvc.perform(MockMvcRequestBuilders.post(API_PATH + "")
                        .with(httpBasic("admin","admin"))
                        .content(objectMapper.writeValueAsString(newPizza))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        //Проверяем что в теле ответа пришел id новой пиццы
        String content_body = result.getResponse().getContentAsString();
        assertThat(content_body).isNotEmpty().isNotBlank().isNotNull();

        //Проверяем что location пришла корректно (Потом скорректировать начало пути http://localhost !!!)
        String content_location = result.getResponse().getHeader("Location");
        assertThat(content_location).isEqualTo("http://localhost" + API_PATH + "/" + content_body);
    }

    //Тестирование метода GetPizzaByIdTest (Получение пиццы по ID)
    @Nested
    @DisplayName("Получение пиццы по id")
    class GetPizzaByIdTest {
        @Test
        @DisplayName("Успешное получение пиццы по id=2")
        void getPizzaById_returnPizzaAndStatus200() throws Exception {
            Long id = 2L;
            mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + "/{id}", id))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.p_id").value(id))
                    .andExpect(jsonPath("$.p_name").value("Pizza_name_02"))
                    .andExpect(jsonPath("$.p_description").value("Description_02"))
                    .andExpect(jsonPath("$.p_base_price").value("11.0"))
                    .andExpect(jsonPath("$.p_photo_link").value("url_02"));
        }

        @Test
        @DisplayName("Пицца по id=400 не найдена")
        public void getPizzaByIdTest_ReturnNotFound() throws Exception {
            Long id = 400L;
            mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + "/{id}", id))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isNotFound());
        }
    }

    //Тестирование метода updatePizzaById (Обновление существующей пиццы по ID)
    @Nested
    @DisplayName("Обновление пиццы по id=3")
    class updatePizzaByIdTest {
        @Test
        @DisplayName("Успешное обновление пиццы по id=3")
        void updatePizzaById_returnPizzaAndStatus200() throws Exception {
            Long id = 3L;
            //Создаем объект пицца с измененными параметрами
            Pizza newPizza = new Pizza();
            newPizza.setP_id(Mockito.any());
            newPizza.setP_name("Pizza_name_03_Update");
            newPizza.setP_description("Description_03_Update");
            newPizza.setP_base_price(50.0);
            newPizza.setP_photo_link("url_03_Update");

            mockMvc.perform(MockMvcRequestBuilders.put(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizza))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.p_id").value(id))
                    .andExpect(jsonPath("$.p_name").value(newPizza.getP_name()))
                    .andExpect(jsonPath("$.p_description").value(newPizza.getP_description()))
                    .andExpect(jsonPath("$.p_base_price").value(newPizza.getP_base_price()))
                    .andExpect(jsonPath("$.p_photo_link").value(newPizza.getP_photo_link()));
        }

        @Test
        @DisplayName("Пицца для обновления по id=400 не найдена")
        public void updatePizzaByIdTest_ReturnNotFound() throws Exception {
            Long id = 400L;
            //Создаем объект пицца с измененными параметрами
            Pizza newPizza = new Pizza();
            newPizza.setP_id(Mockito.any());
            newPizza.setP_name("Pizza_name_03_Update");
            newPizza.setP_description("Description_03_Update");
            newPizza.setP_base_price(50.0);
            newPizza.setP_photo_link("url_03_Update");

            mockMvc.perform(MockMvcRequestBuilders.put(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizza))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isNotFound());
        }
    }

    //Тестирование метода deletePizzaById (Удаление пиццы по ID)
    @Nested
    @DisplayName("Удаление пиццы по id=1")
    class deletePizzaByIdTest {
        @Test
        @DisplayName("Успешное удаление пиццы по id=1")
        void deletePizzaById_returnStatus200() throws Exception {
            Long id = 1L;
            mockMvc.perform(MockMvcRequestBuilders.delete(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin")))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Пицца для удаления по id=400 не найдена")
        public void deletePizzaById_ReturnNotFound() throws Exception {
            Long id = 400L;
            mockMvc.perform(MockMvcRequestBuilders.delete(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin")))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isNotFound());
        }
    }

}
