package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.domain.Pizzeria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface PizzeriaSersice {
    Pizzeria save(Pizzeria newPizza);

    List<Pizzeria> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

    Optional<Pizzeria> findById(Long id);

}
