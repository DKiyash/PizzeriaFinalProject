package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;

import java.util.List;
import java.util.Optional;

public interface PizzaSersice {
    Pizza save(Pizza newPizza);

    List<Pizza> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    Optional<Pizza> findById(Long id);

}
