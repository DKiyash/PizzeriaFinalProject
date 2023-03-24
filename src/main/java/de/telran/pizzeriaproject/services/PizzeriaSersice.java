package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.exeptions.PizzeriaNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface PizzeriaSersice {
    Pizzeria save(Pizzeria newPizza) throws PizzaNotFoundException;

    List<Pizzeria> findAll();

    void deleteById(Long id) throws PizzeriaNotFoundException;

    Optional<Pizzeria> findById(Long id);

    Pizzeria updatePizzeriaById(Long id, Pizzeria newPizzeria) throws PizzeriaNotFoundException, PizzaNotFoundException;
}
