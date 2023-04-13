package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.exeptions.DuplicateEntryException;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.exeptions.PizzeriaNotFoundException;
import de.telran.pizzeriaproject.repositories.PizzaRepositories;
import de.telran.pizzeriaproject.repositories.PizzeriaRepositories;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PizzeriaServiceImpl implements PizzeriaSersice {

    private final PizzeriaRepositories pizzeriaRepositories;
    private final PizzaRepositories pizzaRepositories;

    public PizzeriaServiceImpl(PizzeriaRepositories pizzeriaRepositories, PizzaRepositories pizzaRepositories) {
        this.pizzeriaRepositories = pizzeriaRepositories;
        this.pizzaRepositories = pizzaRepositories;
    }

    //Создание пиццерии
    @Override
    @Transactional
    public Pizzeria save(Pizzeria newPizzeria) throws PizzaNotFoundException, DuplicateEntryException{
        //Проверяем список пицц в сохраняемой пиццерии
        Set<Pizza> pizzaSet = newPizzeria.getPizzaSet();
        //Если список не пустой, то проверяем все ли пиццы из существующего списка пицц
        if(!pizzaSet.isEmpty()){
            for (Pizza pz:pizzaSet) {
                pizzaRepositories.findById(pz.getP_id())
                        .orElseThrow(() -> new PizzaNotFoundException("Pizza not found for id: " + pz.getP_id()));
            }
        }
        //Если все пиццы в списке существуют или список пустой, то создаем/обновляем пиццерию
        newPizzeria.setPr_id(0L);//Что-бы метод не обновил существующую пиццерию
        //Перехватываем DataIntegrityViolationException, которое появляется
        //при попытке сохранения сущности с одинаковыми параметрами
        try{
            return pizzeriaRepositories.save(newPizzeria);
        } catch (DataIntegrityViolationException e){
            throw  new DuplicateEntryException("Pizzeria with these parameters already exists");
        }
    }

    //Получение списка всех пиццерий постранично
    @Override
    @Transactional(readOnly = true)
    public Iterable<Pizzeria> findAll(Pageable pageable) {
        return pizzeriaRepositories.findAll(pageable);
    }

    //Поиск пиццерии по id
    @Override
    @Transactional(readOnly = true)
    public Optional<Pizzeria> findById(Long id) {
        return pizzeriaRepositories.findById(id);
    }

    //Обновление данных о пиццерии
    @Override
    @Transactional
    public Pizzeria updatePizzeriaById(Long id, Pizzeria newPizzeria)
            throws PizzeriaNotFoundException, PizzaNotFoundException {
        //Проверяем, есть ли пиццерия в БД, если нет - выбрасываем exception
        pizzeriaRepositories.findById(id)
                .orElseThrow(() -> new PizzeriaNotFoundException("Pizzeria is not found for id: " + id));
        //Устанавливаем id полученный из пути (вдруг в теле другой id, чтобы не была создана новая пиццерия)
        newPizzeria.setPr_id(id);
        //Проверяем список пицц в сохраняемой пиццерии
        Set<Pizza> pizzaSet = newPizzeria.getPizzaSet();
        //Если список не пустой, то проверяем все ли пиццы из существующего списка пицц
        if(!pizzaSet.isEmpty()){
            for (Pizza pz:pizzaSet) {
                pizzaRepositories.findById(pz.getP_id())
                        .orElseThrow(() -> new PizzaNotFoundException("Pizza is not found for id: " + pz.getP_id()));
            }
        }
        //Пытаемся сохранить новую пиццу, если будет DataIntegrityViolationException,
        // то перехватываем ее в контроллере
        return pizzeriaRepositories.save(newPizzeria);

        //ЭТО ПОЧЕМУ-ТО НЕ РАБОТАЕТ (можно перехватить только уже в контроллере, там работает!!!)
//        //Перехватываем DataIntegrityViolationException, которое появляется
//        //при попытке сохранения сущности с одинаковыми параметрами
//        try{
//            return pizzeriaRepositories.save(newPizzeria);
//        } catch (DataIntegrityViolationException e){
//            throw  new DuplicateEntryException("Pizzeria with these parameters already exists");
//        }
    }

    //Удаление пиццерии
    @Override
    @Transactional
    public void deleteById(Long id) throws PizzeriaNotFoundException{
        //Проверяем, есть ли пиццерия в БД, если нет - выбрасываем exception
        pizzeriaRepositories.findById(id)
                .orElseThrow(() -> new PizzeriaNotFoundException("Pizzeria not found for id: " + id));
        //Удаляем пиццерию
        pizzeriaRepositories.deleteById(id);
    }

}
