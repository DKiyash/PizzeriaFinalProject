package de.telran.pizzeriaproject.domain;

import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pizzeria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pizzeria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pr_id")
    private Long pr_id;

//    @NotEmpty
//    @NotBlank
//    @NotNull
//    @Column(name = "pr_name", unique = true, length = 64)
    @Column(name = "pr_name", length = 64)
    private String pr_name;

//    @NotEmpty
//    @NotBlank
//    @NotNull
//    @Column(name = "pr_address", unique = true, length = 128)
    @Column(name = "pr_address", length = 128)
    private String pr_address;

//    @ManyToMany
//    @JoinTable(name = "pizzeria_pizza", joinColumns = @JoinColumn(name = "pr_id"),
//            inverseJoinColumns = @JoinColumn(name = "p_id"))
//    private Set<Pizza> pizzaSet = new HashSet<>();


    public Pizzeria(String pr_name, String pr_address) {
        this.pr_name = pr_name;
        this.pr_address = pr_address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pizzeria pizzeria)) return false;

        return getPr_id() != null ? getPr_id().equals(pizzeria.getPr_id()) : pizzeria.getPr_id() == null;
    }

    @Override
    public int hashCode() {
        return getPr_id() != null ? getPr_id().hashCode() : 0;
    }
}
