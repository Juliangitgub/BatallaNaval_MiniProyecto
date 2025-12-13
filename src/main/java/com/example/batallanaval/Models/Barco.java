package com.example.batallanaval.Models;

import java.io.Serializable;

/**
 * Representa un barco individual en el juego de Batalla Naval.
 * Almacena su longitud, tipo, orientación y gestiona su estado de daño y hundimiento.
 */
public class Barco implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Longitud del barco, definida por el número de casillas que ocupa. */
    private int longitud;       //
    /** Tipo o nombre del barco (ej. "Portaaviones", "Submarino"). */
    private String tipo;
    /** Contador de los impactos (toques) que ha recibido el barco. */
    private int toques;
    /** Indicador de si el barco está hundido (true) o todavía activo (false). */
    private boolean hundido;    //
    /** Orientación del barco: true para horizontal, false para vertical. */
    private boolean horizontal;

    /**
     * Constructor que inicializa un barco con su longitud y tipo.
     * Inicialmente, no ha recibido impactos, no está hundido y se asume orientación horizontal.
     *
     * @param longitud La longitud del barco en número de casillas.
     * @param tipo El tipo o nombre del barco.
     */
    public Barco(int longitud, String tipo) {
        this.longitud = longitud;
        this.tipo = tipo;
        this.toques = 0;
        this.hundido = false;
        this.horizontal = true;
    }

    /**
     * Registra un nuevo impacto en el barco.
     * Incrementa el contador de toques y verifica si el barco ha sido hundido.
     */
    public void registrarImpacto() {
        this.toques++;
        if (this.toques >= this.longitud) {
            this.hundido = true;
        }
    }

    // Getters y Setters

    /**
     * Verifica si el barco ha sido hundido.
     *
     * @return {@code true} si el número de toques es igual o superior a la longitud del barco, {@code false} en caso contrario.
     */
    public boolean estaHundido() { return hundido; }

    /**
     * Obtiene la longitud del barco.
     *
     * @return La longitud del barco.
     */
    public int getLongitud() { return longitud; }

    /**
     * Obtiene el tipo o nombre del barco.
     *
     * @return El nombre del tipo de barco.
     */
    public String getTipo() { return tipo; }

    /**
     * Verifica si el barco está orientado horizontalmente.
     *
     * @return {@code true} si el barco es horizontal, {@code false} si es vertical.
     */
    public boolean isHorizontal() { return horizontal; }

    /**
     * Establece la orientación del barco.
     *
     * @param horizontal {@code true} para establecer la orientación horizontal, {@code false} para vertical.
     */
    public void setHorizontal(boolean horizontal) { this.horizontal = horizontal; }
}