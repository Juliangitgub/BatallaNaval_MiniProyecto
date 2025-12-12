package com.example.batallanaval.Exceptions;

// Excepción base marcada (Checked Exception)
public class BatallaNavalException extends Exception {
    public BatallaNavalException(String mensaje) {
        super(mensaje);
    }
}