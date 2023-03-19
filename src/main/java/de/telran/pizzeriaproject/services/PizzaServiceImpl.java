package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizza;
import de.telran.pizzeriaproject.repositories.PizzaRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PizzaServiceImpl implements PizzaSersice {

    private final PizzaRepositories pizzaRepositories;

    public PizzaServiceImpl(PizzaRepositories pizzaRepositories) {
        this.pizzaRepositories = pizzaRepositories;
    }

    @Override
    public Pizza save(Pizza newPizza) {
        return pizzaRepositories.save(newPizza);
    }

    @Override
    public List<Pizza> findAll() {
        return pizzaRepositories.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return pizzaRepositories.existsById(id);
    }

    @Override
    public Optional<Pizza> findById(Long id) {
        return pizzaRepositories.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        pizzaRepositories.deleteById(id);
    }

}
