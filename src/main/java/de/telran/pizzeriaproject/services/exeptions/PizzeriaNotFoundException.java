package de.telran.pizzeriaproject.services.exeptions;

public class PizzeriaNotFoundException extends RuntimeException{
    public PizzeriaNotFoundException(String message) {
        super(message);
    }
}
