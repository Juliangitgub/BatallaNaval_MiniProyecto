package com.example.batallanaval.Models;

import java.io.Serializable;

/**
 * Representa una única celda o posición en el tablero de Batalla Naval.
 * Almacena sus coordenadas, si contiene un barco y si ha sido disparada.
 */
public class Casilla implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Coordenada X (fila) de la casilla. */
    private int x;
    /** Coordenada Y (columna) de la casilla. */
    private int y;
    /** Referencia al objeto {@code Barco} que ocupa esta casilla, o {@code null} si está vacía. */
    private Barco barco;
    /** Indicador de si esta casilla ya fue objetivo de un disparo. */
    private boolean disparado; //

    /**
     * Constructor para inicializar una casilla con sus coordenadas.
     * Por defecto, no contiene barco y no ha sido disparada.
     *
     * @param x La coordenada X (fila) de la casilla.
     * @param y La coordenada Y (columna) de la casilla.
     */
    public Casilla(int x, int y) {
        this.x = x;
        this.y = y;
        this.barco = null;
        this.disparado = false;
    }

    /**
     * Verifica si la casilla contiene un barco.
     *
     * @return {@code true} si la casilla tiene un barco, {@code false} en caso contrario.
     */
    public boolean tieneBarco() { return barco != null; }

    /**
     * Obtiene la referencia al barco que ocupa esta casilla.
     *
     * @return El objeto {@code Barco}, o {@code null} si no hay barco.
     */
    public Barco getBarco() { return barco; }

    /**
     * Establece el barco que ocupará esta casilla.
     *
     * @param barco El objeto {@code Barco} a asignar a la casilla.
     */
    public void setBarco(Barco barco) { this.barco = barco; }

    /**
     * Verifica si esta casilla ya fue disparada.
     *
     * @return {@code true} si la casilla ya fue disparada, {@code false} en caso contrario.
     */
    public boolean fueDisparada() { return disparado; }

    /**
     * Establece el estado de disparo de la casilla.
     *
     * @param disparado {@code true} para marcar la casilla como disparada, {@code false} para revertir (usualmente solo {@code true}).
     */
    public void setDisparado(boolean disparado) { this.disparado = disparado; }
}