package com.example.batallanaval.Exceptions;

// Excepción no marcada (Unchecked) para lógica interna
public class MovimientoInvalidoException extends RuntimeException {
    public MovimientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}