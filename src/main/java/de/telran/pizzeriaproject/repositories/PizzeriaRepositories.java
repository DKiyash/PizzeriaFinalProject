package de.telran.pizzeriaproject.repositories;

import de.telran.pizzeriaproject.domain.Pizzeria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PizzeriaRepositories extends JpaRepository<Pizzeria, Long>, PagingAndSortingRepository<Pizzeria, Long> {
}
