package com.example.batallanaval.Models;

import java.io.Serializable;

public class Casilla implements Serializable {
    private static final long serialVersionUID = 1L;
    private int x, y;
    private Barco barco;
    private boolean disparado; // [cite: 24]

    public Casilla(int x, int y) {
        this.x = x;
        this.y = y;
        this.barco = null;
        this.disparado = false;
    }

    public boolean tieneBarco() { return barco != null; }
    public Barco getBarco() { return barco; }
    public void setBarco(Barco barco) { this.barco = barco; }
    public boolean fueDisparada() { return disparado; }
    public void setDisparado(boolean disparado) { this.disparado = disparado; }
}