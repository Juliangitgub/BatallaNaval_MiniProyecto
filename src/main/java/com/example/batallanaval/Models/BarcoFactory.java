package com.example.batallanaval.Models;

/**
 * Clase Factory (Fábrica) encargada de la creación de diferentes tipos de objetos {@code Barco}.
 * Esta clase implementa el Patrón Factory Simple para centralizar la lógica de instanciación
 * de barcos basados en el tipo solicitado.
 */
public class BarcoFactory {

    /**
     * Crea y devuelve una nueva instancia de {@code Barco} con la longitud y el nombre
     * correspondientes al tipo especificado.
     *
     * @param tipo El tipo de barco a crear (ej. "Portaaviones", "Submarino", "Destructor", "Fragata").
     * @return Una nueva instancia del objeto {@code Barco} con las propiedades definidas.
     * @throws IllegalArgumentException Si el tipo de barco solicitado es desconocido.
     */
    public static Barco crearBarco(String tipo) {
        switch (tipo.toLowerCase()) {
            case "portaaviones": return new Barco(4, "Portaaviones"); //
            case "submarino":    return new Barco(3, "Submarino");    //
            case "destructor":   return new Barco(2, "Destructor");   //
            case "fragata":      return new Barco(1, "Fragata");      //
            default: throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        }
    }
}