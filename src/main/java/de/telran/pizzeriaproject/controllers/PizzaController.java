package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.services.PizzaSersice;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Slf4j
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
    ResponseEntity<?> getAllPizzas(Pageable pageable) {
        Iterable<Pizza> pizzaList = pizzaSersice.findAll(pageable);
            return ResponseEntity.ok(pizzaList);
    }

    //Создание новой пиццы
    @PostMapping()
    ResponseEntity<?> createPizza(@Valid @RequestBody Pizza newPizza) {
        //Если новая пицца добавлена, то вернуть код 201 и location (ссылку на пиццу)
        Pizza pizza = pizzaSersice.save(newPizza);
        if (pizza != null) {
            log.info("New Pizza added successfully");
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(pizza.getP_id())
                    .toUri();
            return ResponseEntity.created(location).body(pizza.getP_id());
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
    ResponseEntity<?> updatePizzaById(@PathVariable Long id, @Valid @RequestBody Pizza newPizza) {
        try {
            //Попробовать обновить пиццу
            Pizza updatedPizza = pizzaSersice.updatePizzaById(id, newPizza);
            return ResponseEntity.ok(updatedPizza);
        } catch (PizzaNotFoundException e) {//Если пиццы нет в БД, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        }
    }

    //Удаление пиццы по ID
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deletePizzaById(@PathVariable Long id) {
        try {
            //Попробовать удалить пиццу
            pizzaSersice.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (PizzaNotFoundException e) {//Если пиццы нет в БД, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        }
    }

}
