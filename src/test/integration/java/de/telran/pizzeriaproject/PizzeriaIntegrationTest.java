package de.telran.pizzeriaproject;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Интеграционный тест для сущности Пиццерия (нужно каждый раз удалять и создавать заново БД pizzeria_project в MySQL WorkBench)
@SpringBootTest
//@Sql({
//        "classpath:sql/Create_test_shcema.sql"
////        "classpath:sql/Create_test_shcema.sql",
////        "classpath:sql/Test_data.sql"
//})
public class PizzeriaIntegrationTest extends IntegrationTestsInfrastructureInitializer{

    static final String API_PATH = "/api/v1/pizzerias";

    //Тестирование метода getAllPizzerias (Получение списка всех пиццерий)
    @Test
    @DisplayName("Успешное получение всех пиццерий")
    void getAllPizzerias_returnAllPizzeriasAndStatus200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + ""))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    //Тестирование метода createPizzeria (Создание новой пиццерии)

    @Nested
    @DisplayName("Создание новой пиццерии")
    class CreateNewPizzeriaTest {
        @Test
        @DisplayName("Успешное создание новой пиццерии (без списка пицц)")
        void createPizzeria_WithoutPizzaList_returnIdCreatedPizzeriaAndLocationAndStatus201() throws Exception {
            //Создание случайной строки, т.к. некоторые поля должны быть unique иначе ошибка
            String generatedString = randomString();

            //Создаем новый объект пиццерия (которого нет в базе)
            Pizzeria newPizzeria = new Pizzeria();
            newPizzeria.setPr_id(Mockito.any());
            newPizzeria.setPr_name("Pizzeria_" + generatedString);
            newPizzeria.setPr_address("Address_" + generatedString);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(API_PATH + "")
                            .with(httpBasic("admin", "admin"))
                            .content(objectMapper.writeValueAsString(newPizzeria))
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

        @Test
        @DisplayName("Успешное создание новой пиццерии (со списком существующих пицц)")
        void createPizzeria_WithPizzaList_returnIdCreatedPizzeriaAndLocationAndStatus201() throws Exception {
            //Создание случайной строки, т.к. некоторые поля должны быть unique иначе ошибка
            String generatedString = randomString();

            //Создаем новый объект пиццерия (которого нет в базе)
            Pizzeria newPizzeria = new Pizzeria();
            newPizzeria.setPr_id(Mockito.any());
            newPizzeria.setPr_name("Pizzeria_" + generatedString);
            newPizzeria.setPr_address("Address_" + generatedString);

            //Создаем объект пицца для передачи в теле запроса (Важно: Пиццы должны уже быть в перечне пицц)
            Pizza newPizza1 = new Pizza();
            newPizza1.setP_id(2L);
            newPizzeria.getPizzaSet().add(newPizza1);

            Pizza newPizza2 = new Pizza();
            newPizza2.setP_id(3L);
            newPizzeria.getPizzaSet().add(newPizza2);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(API_PATH + "")
                            .with(httpBasic("admin", "admin"))
                            .content(objectMapper.writeValueAsString(newPizzeria))
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

        @Test
        @DisplayName("Нельзя создать новую пиццерию, если в списке есть несуществующие пиццы")
        void createPizzeria_WithWrongPizzaList_returnStatus400() throws Exception {
            //Создание случайной строки, т.к. некоторые поля должны быть unique иначе ошибка
            String generatedString = randomString();

            //Создаем новый объект пиццерия (которого нет в базе)
            Pizzeria newPizzeria = new Pizzeria();
            newPizzeria.setPr_id(Mockito.any());
            newPizzeria.setPr_name("Pizzeria_" + generatedString);
            newPizzeria.setPr_address("Address_" + generatedString);

            //Создаем объект пицца для передачи в теле запроса (Одна из пицц не должна быть в перечне пицц)
            Pizza newPizza1 = new Pizza();
            newPizza1.setP_id(2L);
            newPizzeria.getPizzaSet().add(newPizza1);

            Pizza newPizza2 = new Pizza();
            newPizza2.setP_id(30L);
            newPizzeria.getPizzaSet().add(newPizza2);

            mockMvc.perform(MockMvcRequestBuilders.post(API_PATH + "")
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizzeria))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Нельзя создать новую пиццерию, если пиццерия с такими параметрами уже существует")
        void createPizzeria_WithDuplicateEntryException_returnStatus409() throws Exception {
            //Создаем объект пиццерия (пицца с такими параметрами уже должна быть в базе)
            Pizzeria newPizzeria = new Pizzeria();
            newPizzeria.setPr_id(Mockito.any());
            newPizzeria.setPr_name("Pizzeria_02");
            newPizzeria.setPr_address("Address_02");

            //Создаем объект пицца для передачи в теле запроса (Важно: Пиццы должны уже быть в перечне пицц)
            Pizza newPizza1 = new Pizza();
            newPizza1.setP_id(2L);
            newPizzeria.getPizzaSet().add(newPizza1);

            Pizza newPizza2 = new Pizza();
            newPizza2.setP_id(3L);
            newPizzeria.getPizzaSet().add(newPizza2);

            mockMvc.perform(MockMvcRequestBuilders.post(API_PATH + "")
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizzeria))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isConflict());
        }

        //Метод для генерации случайной строки
        String randomString(){
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;//Длина генерируемой строки
            Random random = new Random();
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            return buffer.toString();
        }
    }

    //Тестирование метода GetPizzeriaByIdTest (Получение пиццерии по ID)
    @Nested
    @DisplayName("Получение пиццерии по id=2")
    class GetPizzeriaByIdTest {
        @Test
        @DisplayName("Успешное получение пиццерии по id")
        void getPizzeriaById_returnPizzaAndStatus200() throws Exception {
            Long id = 2L;
            mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + "/{id}", id))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.pr_id").value(id))
                    .andExpect(jsonPath("$.pr_name").value("Pizzeria_02"))
                    .andExpect(jsonPath("$.pr_address").value("Address_02"));
        }

        @Test
        @DisplayName("Пиццерия по id не найдена")
        public void getPizzeriaByIdTest_ReturnNotFound() throws Exception {
            Long id = 400L;
            mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + "/{id}", id))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isNotFound());
        }
    }

    //Тестирование метода updatePizzeriaById (Обновление существующей пиццерии по ID)
    @Nested
    @DisplayName("Обновление пиццерии по id=3, id=4")
    class UpdatePizzeriaByIdTest {
        @Test
        @DisplayName("Успешное обновление пиццерии по id=3 (без списка пицц)")
        void updatePizzeriaById_WithoutPizzaList_returnPizzeriaAndStatus200() throws Exception {
            Long id = 3L;
            //Создаем объект пиццерия с измененными параметрами
            Pizzeria newPizzeria = new Pizzeria();
            newPizzeria.setPr_id(Mockito.any());
            newPizzeria.setPr_name("Pizzeria_02_Update");
            newPizzeria.setPr_address("Address_02_Update");

            mockMvc.perform(MockMvcRequestBuilders.put(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizzeria))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.pr_id").value(id))
                    .andExpect(jsonPath("$.pr_name").value(newPizzeria.getPr_name()))
                    .andExpect(jsonPath("$.pr_address").value(newPizzeria.getPr_address()));
        }

        @Test
        @DisplayName("Успешное обновление пиццерии по id=4 (со списком пицц)")
        void updatePizzeriaById_WithPizzaList_returnPizzeriaAndStatus200() throws Exception {
            Long id = 4L;
            //Создаем объект пиццерия с измененными параметрами
            Pizzeria newPizzeria = new Pizzeria();
            newPizzeria.setPr_id(Mockito.any());
            newPizzeria.setPr_name("Pizzeria_03_Update");
            newPizzeria.setPr_address("Address_03_Update");

            //Создаем объект пицца для передачи в теле запроса (Важно: Пиццы должны уже быть в перечне пицц)
            Pizza newPizza1 = new Pizza();
            newPizza1.setP_id(2L);
            newPizzeria.getPizzaSet().add(newPizza1);

            Pizza newPizza2 = new Pizza();
            newPizza2.setP_id(3L);
            newPizzeria.getPizzaSet().add(newPizza2);

            mockMvc.perform(MockMvcRequestBuilders.put(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizzeria))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.pr_id").value(id))
                    .andExpect(jsonPath("$.pr_name").value(newPizzeria.getPr_name()))
                    .andExpect(jsonPath("$.pr_address").value(newPizzeria.getPr_address()))
                    .andExpect(jsonPath("$.pizzaSet.[0].p_id").value(newPizza1.getP_id()))
                    .andExpect(jsonPath("$.pizzaSet.[1].p_id").value(newPizza2.getP_id()));
        }

        @Test
        @DisplayName("Нельзя обновить пиццерию по id=4, если указанных пицц нет в списке пицц")
        void updatePizzeriaById_WithWrongPizzaList_returnStatus400() throws Exception {
            Long id = 4L;
            //Создаем объект пиццерия с измененными параметрами
            Pizzeria newPizzeria = new Pizzeria();
            newPizzeria.setPr_id(Mockito.any());
            newPizzeria.setPr_name("Pizzeria_03_Update_WrongList");
            newPizzeria.setPr_address("Address_03_Update_WrongList");

            //Создаем объект пицца для передачи в теле запроса (Одна из пицц не должна быть в перечне пицц)
            Pizza newPizza1 = new Pizza();
            newPizza1.setP_id(2L);
            newPizzeria.getPizzaSet().add(newPizza1);

            Pizza newPizza2 = new Pizza();
            newPizza2.setP_id(30L);//Такой пиццы нет в списке пицц
            newPizzeria.getPizzaSet().add(newPizza2);

            mockMvc.perform(MockMvcRequestBuilders.put(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizzeria))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Пиццерия для обновления по id=400 не найдена")
        public void updatePizzeriaByIdTest_ReturnNotFound() throws Exception {
            Long id = 400L;
            //Создаем объект пиццерия с измененными параметрами
            Pizzeria newPizzeria = new Pizzeria();
            newPizzeria.setPr_id(Mockito.any());
            newPizzeria.setPr_name("Pizzeria_03_Update");
            newPizzeria.setPr_address("Address_03_Update");

            mockMvc.perform(MockMvcRequestBuilders.put(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizzeria))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isNotFound());
        }
    }

    //Тестирование метода deletePizzeriaById (Удаление пиццерии по ID)
    @Nested
    @DisplayName("Удаление пиццерии по id=1")
    class DeletePizzeriaByIdTest {
        @Test
        @DisplayName("Успешное удаление пиццерии по id=1")
        void deletePizzeriaById_returnStatus200() throws Exception {
            Long id = 1L;
            mockMvc.perform(MockMvcRequestBuilders.delete(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin")))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Пиццерия для удаления по id=400 не найдена")
        public void deletePizzeriaById_ReturnNotFound() throws Exception {
            Long id = 400L;
            mockMvc.perform(MockMvcRequestBuilders.delete(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin")))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isNotFound());
        }
    }
}
