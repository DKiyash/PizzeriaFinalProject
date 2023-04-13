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
    private Long p_id;

    @NotEmpty
    @NotBlank
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "p_name")
    private String p_name;

    @NotEmpty
    @NotBlank
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "p_description")
    private String p_description;

    @NotNull
    @Min(value = 2, message = "The price must be bigger than 2")//Не более чем константная скидка
    @Max(value = 100, message = "The price must be less or equal to 100")
    @Column(name = "p_base_price")
    private Double p_base_price;

    @NotEmpty
    @NotBlank
    @NotNull
    @Column(name = "p_photo_link", length = 2048)
    private String p_photo_link;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "pizzeria_pizza", joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "pizzeria_id"))
    private Set<Pizzeria> pizzeriaSet =  new HashSet<>();


    public Pizza(String p_name, String p_description, Double p_base_price, String p_photo_link) {
        this.p_name = p_name;
        this.p_description = p_description;
        this.p_base_price = p_base_price;
        this.p_photo_link = p_photo_link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pizza pizza)) return false;

        return getP_id() != null ? getP_id().equals(pizza.getP_id()) : pizza.getP_id() == null;
    }

    @Override
    public int hashCode() {
        return getP_id() != null ? getP_id().hashCode() : 0;
    }
}
