package de.telran.pizzeriaproject.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pizzeria", uniqueConstraints = @UniqueConstraint(columnNames = {"pr_name", "pr_address"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pizzeria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pr_id")
    private Long id;

    @NotEmpty
    @NotBlank
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "pr_name")
    private String name;

    @NotEmpty
    @NotBlank
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "pr_address")
    private String address;

    @ManyToMany
    @JoinTable(name = "pizzeria_pizza", joinColumns = @JoinColumn(name = "pizzeria_id"),
            inverseJoinColumns = @JoinColumn(name = "pizza_id"))
    private Set<Pizza> pizzaSet = new HashSet<>();


    public Pizzeria(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pizzeria pizzeria)) return false;

        return getId() != null ? getId().equals(pizzeria.getId()) : pizzeria.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
