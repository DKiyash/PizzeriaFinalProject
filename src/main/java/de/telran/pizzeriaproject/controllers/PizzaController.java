package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.services.PizzaSersice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pizzas")
public class PizzaController {
    private final PizzaSersice pizzaSersice;

    @Autowired
    public PizzaController(PizzaSersice pizzaSersice) {
        this.pizzaSersice = pizzaSersice;
    }

    //Получение списка всех пицц
    @GetMapping()
    ResponseEntity<?> getAllPizzas() {
        //Если список не пустой, то вернуть его
        List<Pizza> pizzaList = pizzaSersice.findAll();
        if (!pizzaList.isEmpty()){
            return ResponseEntity.ok(pizzaList);
        }
        //Если список пустой, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }

    //Создание новой пиццы
    @PostMapping()
    ResponseEntity<?> createPizza(@RequestBody Pizza newPizza) {
        //Если новая пицца добавлена, то вернуть код 201 и location (ссылку на пиццу)
        Pizza pizza = pizzaSersice.save(newPizza);
        if (pizza != null){
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(pizza.getP_id())
                    .toUri();
            return ResponseEntity.created(location).build();
        }
        //Если новая пицца не добавлена, то вернуть "500 Internal Server Error"
        else {
            return ResponseEntity.internalServerError().build();
        }
    }


    //Получение пиццы по ID
    @GetMapping("/{id}")
    ResponseEntity<?> getPizzaById(@PathVariable Long id) {
        Optional<Pizza> result = pizzaSersice.findById(id);
        //Если пицца есть, то вернуть ее
        if (result.isPresent()){
            Pizza pizza = result.get();
            return ResponseEntity.ok(pizza);
        }
        //Если пиццы нет, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }


    //Обновление существующей пиццы
    @PutMapping("/{id}")
    ResponseEntity<?> updatePizzaById(@PathVariable Long id, @RequestBody Pizza newPizza) {
        //Если пицца есть в БД, то обновить ее
        if (pizzaSersice.existsById(id)){
            Pizza event=pizzaSersice.save(newPizza);
            return ResponseEntity.ok(event);
        }
        //Если пиццы нет в БД, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }

    //Удаление пиццы по ID
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deletePizzaById(@PathVariable Long id) {
        //Если пицца есть в БД, то удалить ее
        if (pizzaSersice.existsById(id)){
            pizzaSersice.deleteById(id);
            return ResponseEntity.ok().build();
        }
        //Если пиццы нет в БД, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }

}
