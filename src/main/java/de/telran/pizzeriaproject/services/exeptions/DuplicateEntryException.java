package de.telran.pizzeriaproject.services.exeptions;

public class DuplicateEntryException extends RuntimeException{
    public DuplicateEntryException(String message) {
        super(message);
    }
}
