package de.telran.pizzeriaproject.services.exeptions;

public class PizzaNotFoundException extends RuntimeException{
    public PizzaNotFoundException(String message) {
        super(message);
    }
}
