package com.example.batallanaval.Models;

public class BarcoFactory {
    public static Barco crearBarco(String tipo) {
        switch (tipo.toLowerCase()) {
            case "portaaviones": return new Barco(4, "Portaaviones"); // [cite: 16]
            case "submarino":    return new Barco(3, "Submarino");    // [cite: 17]
            case "destructor":   return new Barco(2, "Destructor");   // [cite: 18]
            case "fragata":      return new Barco(1, "Fragata");      // [cite: 19]
            default: throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        }
    }
}