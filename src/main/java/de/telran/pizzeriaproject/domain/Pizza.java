package de.telran.pizzeriaproject.domain;

import jakarta.persistence.*;
//import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pizza")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Long p_id;

//    @NotEmpty
//    @NotBlank
//    @NotNull
    @Column(name = "p_name", unique = true, length = 64)
    private String p_name;

//    @NotEmpty
//    @NotBlank
//    @NotNull
    @Column(name = "p_description", unique = true, length = 255)
    private String p_description;

//    @NotNull
//    @Min(value = 0, message = "The price must be bigger than 0")
//    @Max(value = 100, message = "The price must be less or equal to 100")
    @Column(name = "p_base_price")
    private Double p_base_price;

//    @NotEmpty
//    @NotBlank
//    @NotNull
    @Column(name = "p_photo_link", length = 2048)
    private String p_photo_link;

//    @ManyToMany (mappedBy = "pizzaSet")
//    private Set<Pizzeria> pizzeriaSet =  new HashSet<>();


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
