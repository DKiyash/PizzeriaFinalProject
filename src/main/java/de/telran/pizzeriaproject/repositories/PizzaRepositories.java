package de.telran.pizzeriaproject.repositories;

import de.telran.pizzeriaproject.domain.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaRepositories extends JpaRepository<Pizza, Long> {
}
