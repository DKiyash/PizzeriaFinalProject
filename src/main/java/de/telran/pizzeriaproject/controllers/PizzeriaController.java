package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.services.PizzeriaSersice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pizzerias")
public class PizzeriaController {
    private final PizzeriaSersice pizzeriaSersice;

    @Autowired
    public PizzeriaController(PizzeriaSersice pizzeriaSersice) {
        this.pizzeriaSersice = pizzeriaSersice;
    }

    //Получение списка всех пиццерий
    @GetMapping()
    ResponseEntity<?> getAllPizzeria() {
        //Если список не пустой, то вернуть его
        List<Pizzeria> pizzeriaList = pizzeriaSersice.findAll();
        if (!pizzeriaList.isEmpty()) {
            return ResponseEntity.ok(pizzeriaList);
        }
        //Если список пустой, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }

    //Создание новой пиццерии
    @PostMapping()
    ResponseEntity<?> createPizzeria(@RequestBody Pizzeria newPizzeria) {
        //Если новая пиццерия добавлена, то вернуть код 201 и location (ссылку на пиццерию)
        Pizzeria pizzeria = pizzeriaSersice.save(newPizzeria);
        if (pizzeria != null) {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(pizzeria.getPr_id())
                    .toUri();
            return ResponseEntity.created(location).build();
        }
        //Если новая пиццерия не добавлена, то вернуть "500 Internal Server Error"
        else {
            return ResponseEntity.internalServerError().build();
        }
    }


    //Получение пиццерии по ID
    @GetMapping("/{id}")
    ResponseEntity<?> getPizzeriaById(@PathVariable Long id) {
        Optional<Pizzeria> result = pizzeriaSersice.findById(id);
        //Если пиццерия есть, то вернуть ее
        if (result.isPresent()){
            Pizzeria pizzeria = result.get();
            return ResponseEntity.ok(pizzeria);
        }
        //Если пиццерии нет, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }


    //Обновление существующей пиццерии
    @PutMapping("/{id}")
    ResponseEntity<?> updatePizzeriaById(@PathVariable Long id, @RequestBody Pizzeria newPizzeria) {
        //Если пиццерия есть в БД, то обновить ее
        if (pizzeriaSersice.existsById(id)) {
            Pizzeria pizzeria = pizzeriaSersice.save(newPizzeria);
            return ResponseEntity.ok(pizzeria);
        }
        //Если пиццерии нет в БД, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }

    //Удаление пиццерии по ID
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deletePizzeriaById(@PathVariable Long id) {
        //Если пиццерия есть в БД, то удалить ее
        if (pizzeriaSersice.existsById(id)) {
            pizzeriaSersice.deleteById(id);
            return ResponseEntity.ok().build();
        }
        //Если пиццерии нет в БД, то вернуть "NOT_FOUND"
        else return ResponseEntity.notFound().build();
    }

}
