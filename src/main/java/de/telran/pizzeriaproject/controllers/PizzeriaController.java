package de.telran.pizzeriaproject.controllers;

import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.exeptions.DuplicateEntryException;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.exeptions.PizzeriaNotFoundException;
import de.telran.pizzeriaproject.services.PizzeriaSersice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/pizzerias")
public class PizzeriaController {
    private final PizzeriaSersice pizzeriaSersice;

    public PizzeriaController(PizzeriaSersice pizzeriaSersice) {
        this.pizzeriaSersice = pizzeriaSersice;
    }

    //Получение списка всех пиццерий
    @Operation(summary = "Get all Pizzerias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Pizzerias",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizzeria.class))})
    })
    @GetMapping()
    ResponseEntity<?> getAllPizzerias(@Parameter(description = "Page parameters, example: {\"page\":0, \"size\":5}")
                                      Pageable pageable) {
        Iterable<Pizzeria> pizzeriaList = pizzeriaSersice.findAll(pageable);
        return ResponseEntity.ok(pizzeriaList);
    }

    //Создание новой пиццерии
    @Operation(summary = "Create a new Pizzeria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created the new Pizzeria",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizzeria.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "400", description = "Pizza list is not correct"),
            @ApiResponse(responseCode = "409", description = "Pizzeria with these parameters already exists")})
    @PostMapping()
    ResponseEntity<?> createPizzeria(@Valid @RequestBody Pizzeria newPizzeria) {
        try {
            //Попробовать создать новую пиццерию
            Pizzeria pizzeria = pizzeriaSersice.save(newPizzeria);
            //Если новая пиццерия добавлена, то вернуть код 201 и location (ссылку на новую пиццерию)
            log.info("New Pizzeria added successfully");
            if (pizzeria != null) {
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(pizzeria.getPr_id())
                        .toUri();
                return ResponseEntity.created(location).body(pizzeria.getPr_id());
            }
            //Если новая пиццерия не добавлена, то вернуть "500 Internal Server Error"
            else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (PizzaNotFoundException e) {//Если список пицц некорректный, то вернуть "BAD_REQUEST"
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DuplicateEntryException e) {//Если Пиццерия с такими параметрами уже существует, то вернуть "CONFLICT"
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    //Получение пиццерии по ID
    @Operation(summary = "Get a Pizzeria by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Pizzeria",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizzeria.class)) }),
            @ApiResponse(responseCode = "404", description = "Pizzeria not found") })
    @GetMapping("/{id}")
    ResponseEntity<?> getPizzeriaById(@Parameter(description = "id of Pizzeria to be searched") @PathVariable Long id) {
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
    @Operation(summary = "Update a Pizzeria по Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated the Pizzeria",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizzeria.class)) }),
            @ApiResponse(responseCode = "404", description = "Pizzeria is not found for id:"),
            @ApiResponse(responseCode = "400", description = "Pizza is not found for id:"),
            @ApiResponse(responseCode = "409", description = "Pizzeria with these parameters already exists")})
    @PutMapping("/{id}")
    ResponseEntity<?> updatePizzeriaById(@Parameter(description = "id of Pizzeria to be searched")
                                         @PathVariable Long id, @Valid @RequestBody Pizzeria newPizzeria) {
        try {
            //Попробовать обновить пиццерию
            Pizzeria updatedPizzeria = pizzeriaSersice.updatePizzeriaById(id, newPizzeria);
            return ResponseEntity.ok(updatedPizzeria);
        } catch (PizzeriaNotFoundException e) {//Если пиццерии нет в списке пиццерий, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        } catch (PizzaNotFoundException e) {//Если список пицц некорректный, то вернуть "BAD_REQUEST"
            return ResponseEntity.badRequest().build();
        } catch (DataIntegrityViolationException e) {//Если Пиццерия с такими параметрами уже существует, то вернуть "CONFLICT"
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Pizzeria with these parameters already exists");
        }
    }

    //Удаление пиццерии по ID
    @Operation(summary = "Delete a Pizzeria by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted the Pizzeria",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pizzeria.class)) }),
            @ApiResponse(responseCode = "404", description = "Pizzeria not found") })
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deletePizzeriaById(@Parameter(description = "id of Pizzeria to be searched")
                                         @PathVariable Long id) {
        try {
            //Попробовать удалить пиццерию
            pizzeriaSersice.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (PizzeriaNotFoundException e) {//Если пиццерии нет в БД, то вернуть "NOT_FOUND"
            return ResponseEntity.notFound().build();
        }
    }

}
