package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.exeptions.DuplicateEntryException;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.services.PizzaSersice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "Get all Pizzas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Pizzas",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizza.class))})
    })
    @GetMapping()
    ResponseEntity<?> getAllPizzas(@Parameter(description = "Page parameters, example: {\"page\":0, \"size\":5}")
                                   Pageable pageable) {
        Iterable<Pizza> pizzaList = pizzaSersice.findAll(pageable);
            return ResponseEntity.ok(pizzaList);
    }

    //Создание новой пиццы
    @Operation(summary = "Create a new Pizza")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the new Pizza",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizza.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "409", description = "Pizza with these parameters already exists")})
    @PostMapping()
    ResponseEntity<?> createPizza(@Valid @RequestBody Pizza newPizza) {
        try {
            //Попробовать создать новую пиццу
            Pizza pizza = pizzaSersice.save(newPizza);
            //Если новая пицца добавлена, то вернуть код 201 и location (ссылку на новую пиццу)
            log.info("New Pizza added successfully");
            if (pizza != null) {
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
        } catch (DuplicateEntryException e) {//Если Пицца с такими параметрами уже существует, то вернуть "CONFLICT"
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    //Получение пиццы по ID
    @Operation(summary = "Get a Pizza by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Pizza",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizza.class)) }),
            @ApiResponse(responseCode = "404", description = "Pizza not found",
                    content = @Content) })
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


    //Обновление существующей пиццы по id
    @Operation(summary = "Update a Pizza по Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the Pizza",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizzeria.class)) }),
            @ApiResponse(responseCode = "404", description = "Pizza is not found"),
            @ApiResponse(responseCode = "409", description = "Pizza with these parameters already exists")})
    @PutMapping("/{id}")
    ResponseEntity<?> updatePizzaById(@Parameter(description = "id of Pizza to be searched")
                                      @PathVariable Long id, @Valid @RequestBody Pizza newPizza) {
        try {
            //Попробовать обновить пиццу
            Pizza updatedPizza = pizzaSersice.updatePizzaById(id, newPizza);
            return ResponseEntity.ok(updatedPizza);
        } catch (PizzaNotFoundException e) {//Если пиццы нет в БД, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException e) {//Если Пицца с такими параметрами уже существует, то вернуть "CONFLICT"
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Pizza with these parameters already exists");
        }
    }

    //Удаление пиццы по ID
    @Operation(summary = "Delete a Pizza by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the Pizza",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizza.class)) }),
            @ApiResponse(responseCode = "404", description = "Pizza not found") })
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deletePizzaById(@Parameter(description = "id of Pizzeria to be searched")
                                      @PathVariable Long id) {
        try {
            //Попробовать удалить пиццу
            pizzaSersice.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (PizzaNotFoundException e) {//Если пиццы нет в БД, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        }
    }

}
