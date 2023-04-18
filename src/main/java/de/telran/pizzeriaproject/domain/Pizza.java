package de.telran.pizzeriaproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pizza", uniqueConstraints = @UniqueConstraint(columnNames = {"p_name", "p_description"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Long id;

    @NotEmpty
    @NotBlank
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "p_name")
    private String name;

    @NotEmpty
    @NotBlank
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "p_description")
    private String description;

    @NotNull
    @Min(value = 2, message = "The price must be bigger than 2")//Не более чем константная скидка
    @Max(value = 100, message = "The price must be less or equal to 100")
    @Column(name = "p_base_price")
    private Double basePrice;

    @NotEmpty
    @NotBlank
    @NotNull
    @Column(name = "p_photo_link", length = 2048)
    private String photoLink;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "pizzeria_pizza", joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "pizzeria_id"))
    private Set<Pizzeria> pizzeriaSet =  new HashSet<>();


    public Pizza(String name, String description, Double basePrice, String photoLink) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.photoLink = photoLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pizza pizza)) return false;

        return getId() != null ? getId().equals(pizza.getId()) : pizza.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
