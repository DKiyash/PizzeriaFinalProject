package de.telran.pizzeriaproject.repositories;

import de.telran.pizzeriaproject.domain.Pizzeria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzeriaRepositories extends JpaRepository<Pizzeria, Long> {
}
