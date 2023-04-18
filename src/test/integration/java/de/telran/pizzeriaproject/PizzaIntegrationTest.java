package de.telran.pizzeriaproject;

import de.telran.pizzeriaproject.domain.Pizza;
import org.junit.jupiter.api.*;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

//Интеграционный тест для сущности Пицца.
//Нужно каждый раз удалять и создавать заново БД pizzeria_project в MySQL WorkBench
//В тестовой БД 4 пицц:
//пицца с id=1 используем для запроса по id и не меняем в ней данные ходе тестов
//пицца с id=2 и 3 используем для update по id, данные в них меняются
//пицца с id=4 используем для delete по id
@SpringBootTest
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
    @Nested
    @DisplayName("Создание новой пиццы")
    class CreateNewPizzaByIdTest {
        @Test
        @DisplayName("Успешное создание новой пиццы")
        void createPizza_returnIdCreatedPizzaAndLocationAndStatus201() throws Exception {
            //Создание случайной строки, т.к. некоторые поля должны быть unique иначе ошибка
            String generatedString = randomString();

            //Создаем новый объект пицца (которого нет в базе)
            Pizza newPizza = new Pizza();
            newPizza.setId(Mockito.any());
            newPizza.setName("Pizza_name_" + generatedString);
            newPizza.setDescription("Description_" + generatedString);
            newPizza.setBasePrice(10.0);
            newPizza.setPhotoLink("url");

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

        @Test
        @DisplayName("Нельзя создать новую пиццу, если пицца с такими параметрами уже существует")
        void createPizza_WithDuplicateEntryException_returnStatus409() throws Exception {
            //Создаем объект пицца (пицца с такими параметрами уже должна быть в базе)
            Pizza newPizza = new Pizza();
            newPizza.setId(Mockito.any());
            newPizza.setName("Pizza_name_01");
            newPizza.setDescription("Description_01");
            newPizza.setBasePrice(10.0);
            newPizza.setPhotoLink("url_01");

            mockMvc.perform(MockMvcRequestBuilders.post(API_PATH + "")
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizza))
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

    //Тестирование метода GetPizzaByIdTest (Получение пиццы по ID)
    @Nested
    @DisplayName("Получение пиццы по id")
    class GetPizzaByIdTest {
        @Test
        @DisplayName("Успешное получение пиццы по id=1")
        void getPizzaById_returnPizzaAndStatus200() throws Exception {
            Long id = 1L;
            mockMvc.perform(MockMvcRequestBuilders.get(API_PATH + "/{id}", id))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value("Pizza_name_01"))
                    .andExpect(jsonPath("$.description").value("Description_01"))
                    .andExpect(jsonPath("$.basePrice").value("10.0"))
                    .andExpect(jsonPath("$.photoLink").value("url_01"));
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
    @DisplayName("Обновление пиццы по id=2")
    class updatePizzaByIdTest {
        @Test
        @DisplayName("Успешное обновление пиццы по id=2")
        void updatePizzaById_returnPizzaAndStatus200() throws Exception {
            Long id = 2L;
            //Создаем объект пицца с измененными параметрами
            Pizza newPizza = new Pizza();
            newPizza.setId(Mockito.any());
            newPizza.setName("Pizza_name_02_Update");
            newPizza.setDescription("Description_02_Update");
            newPizza.setBasePrice(50.0);
            newPizza.setPhotoLink("url_02_Update");

            mockMvc.perform(MockMvcRequestBuilders.put(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizza))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.name").value(newPizza.getName()))
                    .andExpect(jsonPath("$.description").value(newPizza.getDescription()))
                    .andExpect(jsonPath("$.basePrice").value(newPizza.getBasePrice()))
                    .andExpect(jsonPath("$.photoLink").value(newPizza.getPhotoLink()));
        }

        @Test
        @DisplayName("Нельзя обновить  пиццу по id=3, если пицца с такими параметрами уже существует")
        void updatePizza_WithDuplicateEntryException_returnStatus409() throws Exception {
            Long id = 3L;
            //Создаем объект пицца (пицца с такими параметрами уже должна быть в базе)
            Pizza newPizza = new Pizza();
            newPizza.setId(Mockito.any());
            newPizza.setName("Pizza_name_01");
            newPizza.setDescription("Description_01");
            newPizza.setBasePrice(10.0);
            newPizza.setPhotoLink("url_01");

            mockMvc.perform(MockMvcRequestBuilders.put(API_PATH + "/{id}", id)
                            .with(httpBasic("admin","admin"))
                            .content(objectMapper.writeValueAsString(newPizza))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Пицца для обновления по id=400 не найдена")
        public void updatePizzaByIdTest_ReturnNotFound() throws Exception {
            Long id = 400L;
            //Создаем объект пицца с измененными параметрами
            Pizza newPizza = new Pizza();
            newPizza.setId(Mockito.any());
            newPizza.setName("Pizza_name_03_Update");
            newPizza.setDescription("Description_03_Update");
            newPizza.setBasePrice(50.0);
            newPizza.setPhotoLink("url_03_Update");

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
    @DisplayName("Удаление пиццы по id")
    class deletePizzaByIdTest {
        @Test
        @DisplayName("Успешное удаление пиццы по id=4")
        void deletePizzaById_returnStatus200() throws Exception {
            Long id = 4L;
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
