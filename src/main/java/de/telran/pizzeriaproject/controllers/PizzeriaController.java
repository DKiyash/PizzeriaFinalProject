package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.exeptions.PizzeriaNotFoundException;
import de.telran.pizzeriaproject.services.PizzeriaSersice;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/pizzerias")
public class PizzeriaController {
    private final PizzeriaSersice pizzeriaSersice;

    public PizzeriaController(PizzeriaSersice pizzeriaSersice) {
        this.pizzeriaSersice = pizzeriaSersice;
    }

    //Получение списка всех пиццерий
    @GetMapping()
    ResponseEntity<?> getAllPizzerias(Pageable pageable) {
        Iterable<Pizzeria> pizzeriaList = pizzeriaSersice.findAll(pageable);
        return ResponseEntity.ok(pizzeriaList);
    }

    //Создание новой пиццерии
    @PostMapping()
    ResponseEntity<?> createPizzeria(@Valid @RequestBody Pizzeria newPizzeria) {
        try {
            //Попробовать создать пиццерию
            Pizzeria pizzeria = pizzeriaSersice.save(newPizzeria);
            //Если новая пиццерия добавлена, то вернуть код 201 и location (ссылку на пиццерию)
            log.info("New Pizzeria added successfully");
            if (pizzeria != null) {
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(pizzeria.getPr_id())
                        .toUri();
                return ResponseEntity.created(location).body(pizzeria.getPr_id());
            }
            //Если новая пицца не добавлена, то вернуть "500 Internal Server Error"
            else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (PizzaNotFoundException e) {//Если список пицц некорректный, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        }
    }


    //Получение пиццерии по ID
    @GetMapping("/{id}")
    ResponseEntity<?> getPizzeriaById(@PathVariable Long id) {
        Optional<Pizzeria> result = pizzeriaSersice.findById(id);
        //Если пиццерия есть, то вернуть ее
        if (result.isPresent()) {
            Pizzeria pizzeria = result.get();
            return ResponseEntity.ok(pizzeria);
        }
        //Если пиццерии нет, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }

    //Обновление существующей пиццерии
    @PutMapping("/{id}")
    ResponseEntity<?> updatePizzeriaById(@PathVariable Long id, @Valid @RequestBody Pizzeria newPizzeria) {
        try {
            //Попробовать обновить пиццерию
            Pizzeria updatedPizzeria = pizzeriaSersice.updatePizzeriaById(id, newPizzeria);
            return ResponseEntity.ok(updatedPizzeria);
        } catch (PizzeriaNotFoundException e) {//Если пиццерии нет в списке пицерий, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        } catch (PizzaNotFoundException e) {//Если список пицц некорректный, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        }
    }

    //Удаление пиццерии по ID
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deletePizzeriaById(@PathVariable Long id) {
        try {
            //Попробовать удалить пиццерию
            pizzeriaSersice.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (PizzeriaNotFoundException e) {//Если пиццерии нет в БД, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        }
    }

}
