package com.example.batallanaval.Models;

import java.io.Serializable;

public class Barco implements Serializable {
    private static final long serialVersionUID = 1L;

    private int longitud;       // [cite: 15]
    private String tipo;
    private int toques;
    private boolean hundido;    // [cite: 27]
    private boolean horizontal;

    public Barco(int longitud, String tipo) {
        this.longitud = longitud;
        this.tipo = tipo;
        this.toques = 0;
        this.hundido = false;
        this.horizontal = true;
    }

    public void registrarImpacto() {
        this.toques++;
        if (this.toques >= this.longitud) {
            this.hundido = true;
        }
    }

    // Getters y Setters
    public boolean estaHundido() { return hundido; }
    public int getLongitud() { return longitud; }
    public String getTipo() { return tipo; }
    public boolean isHorizontal() { return horizontal; }
    public void setHorizontal(boolean horizontal) { this.horizontal = horizontal; }
}