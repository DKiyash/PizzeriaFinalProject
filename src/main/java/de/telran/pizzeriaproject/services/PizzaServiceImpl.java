package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.exeptions.PizzeriaNotFoundException;
import de.telran.pizzeriaproject.repositories.PizzaRepositories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PizzaServiceImpl implements PizzaSersice {

    private final PizzaRepositories pizzaRepositories;

    public PizzaServiceImpl(PizzaRepositories pizzaRepositories) {
        this.pizzaRepositories = pizzaRepositories;
    }

    //Создание или обновление пиццы
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pizza save(Pizza newPizza) {
        newPizza.setP_id(0L);//Что-бы метод не обновил существующую пиццерию
        return pizzaRepositories.save(newPizza);
    }

    //Получение списка всех пицц
    @Override
    @Transactional(readOnly = true)
    public List<Pizza> findAll() {
        return pizzaRepositories.findAll();
    }

    //Поиск пиццы по id
    @Override
    @Transactional(readOnly = true)
    public Optional<Pizza> findById(Long id) {
        return pizzaRepositories.findById(id);
    }

    //Обновление данных о пицце
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pizza updatePizzaById(Long id, Pizza newPizza) throws PizzaNotFoundException{
        //Проверяем, есть ли пицца в БД, если нет - выбрасываем exception
        pizzaRepositories.findById(id)
                .orElseThrow(() -> new PizzaNotFoundException("Pizza not found for id: " + id));
        //Устанавливаем id полученный из пути (вдруг в теле другой id, чтобы не была создана новая пицца)
        newPizza.setP_id(id);
        //Обновляем данные о пицце
        return pizzaRepositories.save(newPizza);
    }
    //Удаление пиццы
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) throws PizzaNotFoundException{
        //Проверяем, есть ли пицца в БД, если нет - выбрасываем exception
        pizzaRepositories.findById(id)
                .orElseThrow(() -> new PizzaNotFoundException("Pizza not found for id: " + id));
        //Удаляем пиццу
        pizzaRepositories.deleteById(id);
    }

}
