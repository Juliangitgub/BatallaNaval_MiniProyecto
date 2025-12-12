package com.example.batallanaval.Models;

import com.example.batallanaval.Exceptions.MovimientoInvalidoException;
import java.io.Serializable;

public class Tablero implements Serializable {
    private static final long serialVersionUID = 1L;
    private Casilla[][] casillas; // Matriz 10x10 [cite: 9]

    public Tablero() {
        casillas = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casillas[i][j] = new Casilla(i, j);
            }
        }
    }

    public Casilla[][] getCasillas() { return casillas; }

    public void colocarBarco(Barco barco, int x, int y, boolean horizontal) {
        if (!esPosicionValida(barco.getLongitud(), x, y, horizontal)) {
            throw new MovimientoInvalidoException("Posición inválida o superpuesta"); //
        }

        barco.setHorizontal(horizontal);
        for (int i = 0; i < barco.getLongitud(); i++) {
            if (horizontal) casillas[x + i][y].setBarco(barco);
            else casillas[x][y + i].setBarco(barco);
        }
    }

    public boolean esPosicionValida(int longitud, int x, int y, boolean horizontal) {
        // [cite: 103] Validación de límites y superposición
        if (horizontal) {
            if (x + longitud > 10) return false;
            for (int i = 0; i < longitud; i++) if (casillas[x + i][y].tieneBarco()) return false;
        } else {
            if (y + longitud > 10) return false;
            for (int i = 0; i < longitud; i++) if (casillas[x][y + i].tieneBarco()) return false;
        }
        return true;
    }

    // 0=Agua, 1=Tocado, 2=Hundido, 3=Repetido
    public int recibirDisparo(int x, int y) {
        if (x < 0 || x >= 10 || y < 0 || y >= 10) return 3;
        Casilla casilla = casillas[x][y];

        if (casilla.fueDisparada()) return 3;

        casilla.setDisparado(true);
        if (!casilla.tieneBarco()) return 0; // Agua [cite: 22]

        Barco barco = casilla.getBarco();
        barco.registrarImpacto();
        return barco.estaHundido() ? 2 : 1; // [cite: 24, 27]
    }
    // Método para verificar si PERDÍ (todos mis barcos están hundidos)
    public boolean estaDerrotado() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Casilla casilla = casillas[i][j];
                if (casilla.tieneBarco()) {
                    // Si encuentro UN barco que NO está hundido, todavía estoy vivo
                    if (!casilla.getBarco().estaHundido()) {
                        return false;
                    }
                }
            }
        }
        // Si recorrí todo y no encontré barcos vivos, estoy derrotado
        return true;
    }
}