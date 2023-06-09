package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.services.exeptions.PizzaNotFoundException;
import de.telran.pizzeriaproject.services.exeptions.PizzeriaNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface PizzeriaService {
    Pizzeria save(Pizzeria newPizza) throws PizzaNotFoundException;

    List<Pizzeria> findAll(Pageable pageable);

    void deleteById(Long id) throws PizzeriaNotFoundException;

    Optional<Pizzeria> findById(Long id);

    Pizzeria updatePizzeriaById(Long id, Pizzeria newPizzeria) throws PizzeriaNotFoundException, PizzaNotFoundException;
}
