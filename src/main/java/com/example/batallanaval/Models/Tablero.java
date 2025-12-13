package com.example.batallanaval.Models;

import com.example.batallanaval.Exceptions.MovimientoInvalidoException;
import java.io.Serializable;

/**
 * Representa el tablero de juego de Batalla Naval, que es una cuadrícula de 10x10.
 * Se encarga de la gestión de las casillas, la colocación de barcos y el procesamiento de disparos.
 */
public class Tablero implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Matriz de {@code Casilla} que representa las 10x10 posiciones del tablero.
     */
    private Casilla[][] casillas; // Matriz 10x10

    /**
     * Constructor que inicializa el tablero creando una matriz de 10x10 y llenándola
     * con objetos {@code Casilla} en cada posición.
     */
    public Tablero() {
        casillas = new Casilla[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                casillas[i][j] = new Casilla(i, j);
            }
        }
    }

    /**
     * Obtiene la matriz de casillas que componen el tablero.
     *
     * @return La matriz bidimensional de {@code Casilla}.
     */
    public Casilla[][] getCasillas() { return casillas; }

    /**
     * Coloca un barco en el tablero a partir de una posición inicial y una orientación.
     *
     * @param barco El objeto {@code Barco} a colocar.
     * @param x La coordenada X (fila) de la posición inicial.
     * @param y La coordenada Y (columna) de la posición inicial.
     * @param horizontal {@code true} si el barco se coloca horizontalmente, {@code false} si es vertical.
     * @throws MovimientoInvalidoException Si la posición no es válida (fuera de límites o superpuesta a otro barco).
     */
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

    /**
     * Verifica si una posición y orientación para un barco son válidas en el tablero.
     * La validación incluye verificar límites y la ausencia de superposición con otros barcos.
     *
     * @param longitud La longitud del barco a colocar.
     * @param x La coordenada X (fila) de la posición inicial.
     * @param y La coordenada Y (columna) de la posición inicial.
     * @param horizontal {@code true} si el barco se intenta colocar horizontalmente.
     * @return {@code true} si la posición es válida, {@code false} en caso contrario.
     */
    public boolean esPosicionValida(int longitud, int x, int y, boolean horizontal) {
        // Validación de límites y superposición
        if (horizontal) {
            if (x + longitud > 10) return false;
            for (int i = 0; i < longitud; i++) if (casillas[x + i][y].tieneBarco()) return false;
        } else {
            if (y + longitud > 10) return false;
            for (int i = 0; i < longitud; i++) if (casillas[x][y + i].tieneBarco()) return false;
        }
        return true;
    }

    /**
     * Procesa un disparo en las coordenadas especificadas.
     *
     * @param x La coordenada X (fila) del disparo.
     * @param y La coordenada Y (columna) del disparo.
     * @return Un código que indica el resultado del disparo:
     * 0 = Agua (fallo), 1 = Tocado (hit), 2 = Hundido (sunk), 3 = Repetido (ya se disparó o coordenadas fuera de límites).
     */
    // 0=Agua, 1=Tocado, 2=Hundido, 3=Repetido
    public int recibirDisparo(int x, int y) {
        if (x < 0 || x >= 10 || y < 0 || y >= 10) return 3;
        Casilla casilla = casillas[x][y];

        if (casilla.fueDisparada()) return 3;

        casilla.setDisparado(true);
        if (!casilla.tieneBarco()) return 0; // Agua

        Barco barco = casilla.getBarco();
        barco.registrarImpacto();
        return barco.estaHundido() ? 2 : 1; //
    }

    /**
     * Verifica si el jugador propietario de este tablero ha sido derrotado (todos sus barcos están hundidos).
     *
     * @return {@code true} si todos los barcos en el tablero están hundidos, {@code false} en caso contrario.
     */
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