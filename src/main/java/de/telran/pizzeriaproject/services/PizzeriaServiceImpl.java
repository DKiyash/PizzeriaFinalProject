package de.telran.pizzeriaproject.services;

import de.telran.pizzeriaproject.domain.Pizzeria;
import de.telran.pizzeriaproject.repositories.PizzeriaRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PizzeriaServiceImpl implements PizzeriaSersice {

    private final PizzeriaRepositories pizzeriaRepositories;

    public PizzeriaServiceImpl(PizzeriaRepositories pizzeriaRepositories) {
        this.pizzeriaRepositories = pizzeriaRepositories;
    }

    @Override
    public Pizzeria save(Pizzeria newPizza) {
        return pizzeriaRepositories.save(newPizza);
    }

    @Override
    public List<Pizzeria> findAll() {
        return pizzeriaRepositories.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return pizzeriaRepositories.existsById(id);
    }

    @Override
    public Optional<Pizzeria> findById(Long id) {
        return pizzeriaRepositories.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        pizzeriaRepositories.deleteById(id);
    }

}
