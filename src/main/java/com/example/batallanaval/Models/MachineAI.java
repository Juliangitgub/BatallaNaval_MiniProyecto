package com.example.batallanaval.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa la Inteligencia Artificial de la máquina en el juego de Batalla Naval.
 * Se encarga de la colocación de su flota y de decidir la posición de sus disparos
 * utilizando el Patrón Strategy para alternar entre el modo Patrulla (aleatorio) y el modo Caza (dirigido).
 */
public class MachineAI implements Serializable {
    private static final long serialVersionUID = 1L;

    /** El tablero de juego de la IA, donde están colocados sus barcos. */
    private Tablero tablero;

    /** Generador de números aleatorios utilizado para la colocación de barcos y el modo Patrulla. */
    private Random random;

    /** * Memoria de la IA para almacenar coordenadas vecinas a un impacto exitoso.
     * Se utiliza cuando la IA está en el modo Caza.
     */
    private List<int[]> colaDeCaza; // Memoria para modo Caza

    // PATRÓN STRATEGY: La interfaz de comportamiento
    /**
     * Interfaz que define el comportamiento para decidir la posición de un disparo
     * contra el tablero del oponente.
     */
    private interface AttackStrategy extends Serializable {
        /**
         * Ejecuta la lógica específica de la estrategia para seleccionar la siguiente casilla a atacar.
         *
         * @param oponente El tablero del jugador oponente.
         * @param random El generador de números aleatorios.
         * @param colaDeCaza La cola de memoria utilizada por la estrategia de caza.
         * @return Un arreglo de enteros {@code int[]} con las coordenadas [x, y] del disparo.
         */
        int[] ejecutar(Tablero oponente, Random random, List<int[]> colaDeCaza);
    }

    // ESTRATEGIA 1: Disparo Aleatorio (Modo Patrulla)
    /**
     * Implementa la estrategia de disparo aleatorio.
     * Selecciona una casilla no disparada al azar en todo el tablero.
     */
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
    /**
     * Implementa la estrategia de caza.
     * Prioriza disparar a las casillas vecinas almacenadas en la memoria (colaDeCaza)
     * después de un impacto exitoso. Si la cola está vacía o las coordenadas son inválidas,
     * retorna a la estrategia de disparo aleatorio.
     */
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

    /**
     * Constructor que inicializa la IA, crea su tablero, el generador de números
     * aleatorios y la cola de caza. Llama a {@code inicializarFlota()} para colocar los barcos.
     */
    public MachineAI() {
        this.tablero = new Tablero();
        this.random = new Random();
        this.colaDeCaza = new ArrayList<>();
        inicializarFlota();
    }

    /**
     * Obtiene el tablero de juego de la IA.
     *
     * @return El objeto {@code Tablero} de la máquina.
     */
    public Tablero getTablero() { return tablero; }

    /**
     * Coloca la flota completa de barcos de la IA en su tablero de manera aleatoria,
     * asegurando que todos los barcos se coloquen en posiciones válidas.
     */
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

    /**
     * Decide y ejecuta la estrategia de ataque para determinar las coordenadas del próximo disparo.
     * La estrategia cambia de Patrulla (aleatorio) a Caza (memoria) si hay impactos exitosos pendientes
     * en la {@code colaDeCaza}.
     *
     * @param tableroOponente El tablero del jugador oponente que será atacado.
     * @return Un arreglo de enteros {@code int[]} con las coordenadas [x, y] del disparo seleccionado.
     */
    // Método principal que decide la estrategia dinámicamente
    public int[] realizarDisparo(Tablero tableroOponente) {
        AttackStrategy strategy;

        // Decisión de cambio de Estrategia en tiempo de ejecución
        if (colaDeCaza.isEmpty()) {
            strategy = new RandomShotStrategy(); // Comportamiento A (Patrulla)
        } else {
            strategy = new HuntShotStrategy();   // Comportamiento B (Caza)
        }

        return strategy.ejecutar(tableroOponente, random, colaDeCaza);
    }

    /**
     * Registra un impacto exitoso en el tablero oponente,
     * añadiendo las casillas vecinas (N, S, E, O) a la {@code colaDeCaza}
     * para iniciar el modo Caza en el siguiente turno.
     *
     * @param x La coordenada X (fila) del impacto exitoso.
     * @param y La coordenada Y (columna) del impacto exitoso.
     */
    public void registrarImpactoExitoso(int x, int y) {
        // Agregar vecinos a la memoria (Norte, Sur, Este, Oeste)
        colaDeCaza.add(new int[]{x + 1, y});
        colaDeCaza.add(new int[]{x - 1, y});
        colaDeCaza.add(new int[]{x, y + 1});
        colaDeCaza.add(new int[]{x, y - 1});
    }
}