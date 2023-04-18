package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.exeptions.PizzaNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PizzaService {
    Pizza save(Pizza newPizza);

    List<Pizza> findAll(Pageable pageable);
    List<Pizza> findAll();

    void deleteById(Long id) throws PizzaNotFoundException;

    Optional<Pizza> findById(Long id);

    Pizza updatePizzaById(Long id, Pizza newPizza) throws PizzaNotFoundException;

}
