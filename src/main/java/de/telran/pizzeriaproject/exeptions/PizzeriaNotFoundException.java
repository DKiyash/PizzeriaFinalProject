package de.telran.pizzeriaproject.exeptions;

public class PizzeriaNotFoundException extends RuntimeException{
    public PizzeriaNotFoundException(String message) {
        super(message);
    }
}
