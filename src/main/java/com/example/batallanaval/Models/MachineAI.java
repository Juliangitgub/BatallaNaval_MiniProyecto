package com.example.batallanaval.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Contexto del Patrón Strategy
public class MachineAI implements Serializable {
    private static final long serialVersionUID = 1L;
    private Tablero tablero;
    private Random random;
    private List<int[]> colaDeCaza; // Memoria para modo Caza

    // PATRÓN STRATEGY: La interfaz de comportamiento
    private interface AttackStrategy extends Serializable {
        int[] ejecutar(Tablero oponente, Random random, List<int[]> colaDeCaza);
    }

    // ESTRATEGIA 1: Disparo Aleatorio (Modo Patrulla)
    private static class RandomShotStrategy implements AttackStrategy {
        @Override
        public int[] ejecutar(Tablero oponente, Random random, List<int[]> colaDeCaza) {
            int x, y;
            do {
                x = random.nextInt(10);
                y = random.nextInt(10);
            } while (oponente.getCasillas()[x][y].fueDisparada());
            return new int[]{x, y};
        }
    }

    // ESTRATEGIA 2: Disparo de Caza (Modo Destrucción)
    private static class HuntShotStrategy implements AttackStrategy {
        @Override
        public int[] ejecutar(Tablero oponente, Random random, List<int[]> colaDeCaza) {
            // Intentar disparar a las coordenadas en la cola
            while (!colaDeCaza.isEmpty()) {
                int[] coords = colaDeCaza.remove(0);
                int x = coords[0];
                int y = coords[1];

                // Validar que esté dentro del tablero y no se haya disparado ya
                if (x >= 0 && x < 10 && y >= 0 && y < 10) {
                    if (!oponente.getCasillas()[x][y].fueDisparada()) {
                        return new int[]{x, y};
                    }
                }
            }
            // Si la cola se vacía o las coordenadas no sirven, volvemos a random
            return new RandomShotStrategy().ejecutar(oponente, random, colaDeCaza);
        }
    }

    public MachineAI() {
        this.tablero = new Tablero();
        this.random = new Random();
        this.colaDeCaza = new ArrayList<>();
        inicializarFlota();
    }

    public Tablero getTablero() { return tablero; }

    private void inicializarFlota() {
        Barco[] flota = {
                BarcoFactory.crearBarco("Portaaviones"),
                BarcoFactory.crearBarco("Submarino"), BarcoFactory.crearBarco("Submarino"),
                BarcoFactory.crearBarco("Destructor"), BarcoFactory.crearBarco("Destructor"), BarcoFactory.crearBarco("Destructor"),
                BarcoFactory.crearBarco("Fragata"), BarcoFactory.crearBarco("Fragata"), BarcoFactory.crearBarco("Fragata"), BarcoFactory.crearBarco("Fragata")
        };

        for (Barco barco : flota) {
            boolean puesto = false;
            while (!puesto) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);
                boolean horiz = random.nextBoolean();
                try {
                    tablero.colocarBarco(barco, x, y, horiz);
                    puesto = true;
                } catch (Exception e) {}
            }
        }
    }

    // Método principal que decide la estrategia dinámicamente
    public int[] realizarDisparo(Tablero tableroOponente) {
        AttackStrategy strategy;

        // Decisión de cambio de Estrategia en tiempo de ejecución
        if (colaDeCaza.isEmpty()) {
            strategy = new RandomShotStrategy(); // Comportamiento A
        } else {
            strategy = new HuntShotStrategy();   // Comportamiento B
        }

        return strategy.ejecutar(tableroOponente, random, colaDeCaza);
    }

    public void registrarImpactoExitoso(int x, int y) {
        // Agregar vecinos a la memoria (Norte, Sur, Este, Oeste)
        colaDeCaza.add(new int[]{x + 1, y});
        colaDeCaza.add(new int[]{x - 1, y});
        colaDeCaza.add(new int[]{x, y + 1});
        colaDeCaza.add(new int[]{x, y - 1});
    }
}