package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;

import java.util.List;
import java.util.Optional;

public interface PizzaSersice {
    Pizza save(Pizza newPizza);

    List<Pizza> findAll();

    void deleteById(Long id) throws PizzaNotFoundException;

    Optional<Pizza> findById(Long id);

    Pizza updatePizzaById(Long id, Pizza newPizza) throws PizzaNotFoundException;

}
