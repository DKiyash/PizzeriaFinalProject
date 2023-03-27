package de.telran.pizzeriaproject.repositories;

import de.telran.pizzeriaproject.domain.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PizzaRepositories extends JpaRepository<Pizza, Long>, PagingAndSortingRepository<Pizza, Long> {
}
