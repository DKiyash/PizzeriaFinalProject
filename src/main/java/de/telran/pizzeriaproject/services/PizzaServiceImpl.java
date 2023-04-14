package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.exeptions.DuplicateEntryException;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.exeptions.PizzeriaNotFoundException;
import de.telran.pizzeriaproject.repositories.PizzaRepositories;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PizzaServiceImpl implements PizzaSersice {

    private final PizzaRepositories pizzaRepositories;

    public PizzaServiceImpl(PizzaRepositories pizzaRepositories) {
        this.pizzaRepositories = pizzaRepositories;
    }

    //Создание или обновление пиццы
    @Override
    @Transactional
    public Pizza save(Pizza newPizza) throws DuplicateEntryException {
        newPizza.setP_id(0L);//Что-бы метод не обновил существующую пиццу
        //Перехватываем DataIntegrityViolationException, которое появляется
        //при попытке сохранения сущности с одинаковыми параметрами
        try{
            return pizzaRepositories.save(newPizza);
        } catch (DataIntegrityViolationException e){
            throw  new DuplicateEntryException("Pizza with these parameters already exists");
        }
    }

    //Получение списка всех пицц постранично (используется в контроллере)
    @Override
    @Transactional(readOnly = true)
    public List<Pizza> findAll(Pageable pageable) {
        return pizzaRepositories.findAll(pageable).getContent();
    }

    //Получение списка всех пицц постранично (используется в PizzaPriceScheduler)
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
    @Transactional
    public Pizza updatePizzaById(Long id, Pizza newPizza) throws PizzaNotFoundException {
        //Проверяем, есть ли пицца в БД, если нет - выбрасываем exception
        pizzaRepositories.findById(id)
                .orElseThrow(() -> new PizzaNotFoundException("Pizza is not found for id: " + id));
        //Устанавливаем id полученный из пути (вдруг в теле другой id, чтобы не была создана новая пицца)
        newPizza.setP_id(id);
        //Пытаемся сохранить новую пиццу, если будет DataIntegrityViolationException,
        // то перехватываем ее в контроллере
        return pizzaRepositories.save(newPizza);

        //ЭТО ПОЧЕМУ-ТО НЕ РАБОТАЕТ (можно перехватить только уже в контроллере, там работает!!!)
////        Перехватываем DataIntegrityViolationException, которое появляется
////        при попытке сохранения сущности с одинаковыми параметрами
//        try{
//            return pizzaRepositories.save(newPizza);
//        } catch (DataIntegrityViolationException e){
//            throw  new DuplicateEntryException("Pizza with these parameters already exists");
//        }
    }
    //Удаление пиццы
    @Override
    @Transactional
    public void deleteById(Long id) throws PizzaNotFoundException{
        //Проверяем, есть ли пицца в БД, если нет - выбрасываем exception
        pizzaRepositories.findById(id)
                .orElseThrow(() -> new PizzaNotFoundException("Pizza not found for id: " + id));
        //Удаляем пиццу
        pizzaRepositories.deleteById(id);
    }

}
