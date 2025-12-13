package com.example.batallanaval.Views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Clase encargada de dibujar todos los elementos visuales del juego
 * Batalla Naval (barcos, marcas de impacto, marcas de agua, etc.) utilizando JavaFX Canvas.
 */
public class GamePainter {

    private static final int CELL_SIZE = 40;

    /* =====================================================
     * MARCAS DE JUEGO (AGUA / HIT / HUNDIDO / EXPLOSION)
     * ===================================================== */

    /**
     * Dibuja la marca visual que indica un disparo fallido (agua).
     *
     * @return Un objeto {@code Canvas} que contiene la representación gráfica de un disparo en el agua.
     */
    public Canvas dibujarAgua() {
        Canvas c = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext g = c.getGraphicsContext2D();

        // Fondo azul claro para representar agua
        g.setFill(Color.rgb(173, 216, 230, 0.3)); // Azul agua semitransparente
        g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);

        // Dibujar una X grande que ocupe la mayor parte de la celda
        g.setStroke(Color.BLUE);
        g.setLineWidth(3);

        // X - primera línea (de esquina superior izquierda a inferior derecha)
        g.strokeLine(8, 8, CELL_SIZE - 8, CELL_SIZE - 8);

        // X - segunda línea (de esquina superior derecha a inferior izquierda)
        g.strokeLine(CELL_SIZE - 8, 8, 8, CELL_SIZE - 8);

        // Opcional: Contorno de la celda para mayor claridad
        g.setStroke(Color.LIGHTBLUE);
        g.setLineWidth(1);
        g.strokeRect(0, 0, CELL_SIZE, CELL_SIZE);

        return c;
    }

    /**
     * Dibuja la marca visual que indica un impacto (hit) en un barco.
     *
     * @return Un objeto {@code Canvas} que contiene la representación gráfica de un impacto.
     */
    public Canvas dibujarImpacto() {
        Canvas c = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext g = c.getGraphicsContext2D();

        g.setFill(Color.TRANSPARENT);
        g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);

        g.setFill(Color.rgb(0, 0, 0, 0.3));
        g.fillOval(8, 8, 24, 24);

        g.setFill(Color.DARKGRAY);
        g.fillOval(10, 10, 20, 20);

        g.setFill(Color.GRAY);
        g.fillOval(13, 13, 14, 14);

        g.setFill(Color.BLACK);
        g.fillOval(16, 16, 8, 8);

        return c;
    }

    /**
     * Dibuja una animación o marca visual para una explosión.
     *
     * @return Un objeto {@code Canvas} que contiene la representación gráfica de una explosión.
     */
    public Canvas dibujarExplosion() {
        Canvas c = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext g = c.getGraphicsContext2D();

        g.setFill(Color.TRANSPARENT);
        g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);

        g.setFill(Color.YELLOW);
        g.fillOval(4, 4, 32, 32);

        g.setFill(Color.ORANGERED);
        g.fillOval(10, 10, 20, 20);

        g.setStroke(Color.DARKRED);
        g.setLineWidth(3);
        g.strokeLine(20, 0, 20, 12);
        g.strokeLine(20, 28, 20, 40);
        g.strokeLine(0, 20, 12, 20);
        g.strokeLine(28, 20, 40, 20);

        return c;
    }

    /* =====================================================
     * BARCOS
     * ===================================================== */

    /**
     * Dibuja la representación completa de un barco en el tablero, incluyendo su forma,
     * sombra y detalles específicos de su tipo.
     *
     * @param tipo El tipo de barco (ej. "portaaviones", "submarino", etc.) para aplicar detalles.
     * @param longitud La longitud del barco en número de celdas.
     * @param horizontal {@code true} si el barco está orientado horizontalmente, {@code false} si es vertical.
     * @return Un objeto {@code Canvas} que contiene el dibujo del barco completo.
     */
    public Canvas dibujarBarco(String tipo, int longitud, boolean horizontal) {
        int w = horizontal ? longitud * CELL_SIZE : CELL_SIZE;
        int h = horizontal ? CELL_SIZE : longitud * CELL_SIZE;

        Canvas canvas = new Canvas(w + 3, h + 3); // Aumentar la sombra
        GraphicsContext g = canvas.getGraphicsContext2D();

        g.setFill(Color.TRANSPARENT);
        g.fillRect(0, 0, w + 3, h + 3);

        g.setFill(Color.rgb(0, 0, 0, 0.4));
        g.fillRoundRect(3, 3, w, h, 14, 14);


        g.setFill(Color.LIGHTGRAY);
        g.fillRoundRect(0, 0, w, h, 14, 14);

        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        g.strokeRoundRect(1, 1, w - 2, h - 2, 14, 14);

        // Dibuja las líneas internas de la cuadrícula
        g.setStroke(Color.DARKGRAY);
        g.setLineWidth(1);
        if (horizontal) {
            for (int i = 1; i < longitud; i++) {
                int x = i * CELL_SIZE;
                g.strokeLine(x, 0, x, CELL_SIZE);
            }
        } else {
            for (int i = 1; i < longitud; i++) {
                int y = i * CELL_SIZE;
                g.strokeLine(0, y, CELL_SIZE, y);
            }
        }

        dibujarDetallesPorTipo(g, tipo, longitud, horizontal);

        return canvas;
    }

    /* =====================================================
     * MÉTODOS PRIVADOS DE APOYO
     * ===================================================== */

    /**
     * Dibuja los detalles específicos (ventanas, torre, cañón, antena) según el tipo de barco.
     *
     * @param g El contexto gráfico del Canvas donde se dibujará.
     * @param tipo El tipo de barco.
     * @param longitud La longitud del barco.
     * @param horizontal {@code true} si el barco es horizontal.
     */
    private void dibujarDetallesPorTipo(GraphicsContext g, String tipo, int longitud, boolean horizontal) {
        switch (tipo.toLowerCase()) {
            case "portaaviones":
                dibujarVentanas(g, longitud, horizontal);
                break;
            case "submarino":
                dibujarTorreSubmarino(g, horizontal);
                break;
            case "destructor":
                dibujarCanon(g, horizontal);
                break;
            case "fragata":
                dibujarAntena(g, horizontal);
                break;
        }
    }

    /**
     * Dibuja las ventanas o luces del Portaaviones con un efecto de resplandor.
     *
     * @param g El contexto gráfico del Canvas.
     * @param longitud La longitud del barco.
     * @param horizontal {@code true} si el barco es horizontal.
     */
    private void dibujarVentanas(GraphicsContext g, int longitud, boolean horizontal) {
        int spacing = (longitud * CELL_SIZE) / (4);

        for (int i = 1; i <= 3; i++) {
            // Coordenadas del centro
            double centerX = horizontal ? spacing * i : CELL_SIZE / 2.0;
            double centerY = horizontal ? CELL_SIZE / 2.0 : spacing * i;

            // 1. Halo exterior (color de luz tenue para el resplandor)
            g.setFill(Color.AQUA.deriveColor(1, 1, 1, 0.5)); // Azul claro semitransparente
            g.fillOval(centerX - 8, centerY - 8, 16, 16);

            // 2. Luz central (el punto brillante)
            g.setFill(Color.rgb(100, 149, 237)); // Cornflower Blue
            g.fillOval(centerX - 5, centerY - 5, 10, 10);
        }
    }

    /**
     * Dibuja la torre de mando del Submarino.
     *
     * @param g El contexto gráfico del Canvas.
     * @param horizontal {@code true} si el barco es horizontal.
     */
    private void dibujarTorreSubmarino(GraphicsContext g, boolean horizontal) {
        g.setFill(Color.DARKGRAY.darker().darker());

        if (horizontal) {
            // La torre se ve ligeramente más pequeña y centrada en la celda
            g.fillRect(CELL_SIZE + 5, 5, 30, 30); // Usando 30x30 en la segunda celda
        } else {
            // Asumiendo que va en la segunda celda también
            g.fillRect(5, CELL_SIZE + 5, 30, 30);
        }
    }

    /**
     * Dibuja el cañón principal del Destructor.
     *
     * @param g El contexto gráfico del Canvas.
     * @param horizontal {@code true} si el barco es horizontal.
     */
    private void dibujarCanon(GraphicsContext g, boolean horizontal) {
        g.setFill(Color.BLACK);

        if (horizontal) {
            // Base del cañón (más grueso en el barco)
            g.fillRect(CELL_SIZE * 2 - 20, CELL_SIZE / 2.0 - 10, 10, 20);
            // Cañón saliente (más largo y fino)
            g.fillRect(CELL_SIZE * 2 - 10, CELL_SIZE / 2.0 - 3, 12, 6);
        } else {
            // Base del cañón (más grueso en el barco)
            g.fillRect(CELL_SIZE / 2.0 - 10, CELL_SIZE * 2 - 20, 20, 10);
            // Cañón saliente (más largo y fino)
            g.fillRect(CELL_SIZE / 2.0 - 3, CELL_SIZE * 2 - 10, 6, 12);
        }
    }

    /**
     * Dibuja la antena o mástil de la Fragata.
     *
     * @param g El contexto gráfico del Canvas.
     * @param horizontal {@code true} si el barco es horizontal.
     */
    private void dibujarAntena(GraphicsContext g, boolean horizontal) {
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);

        if (horizontal)
            g.strokeLine(CELL_SIZE / 2.0, 4, CELL_SIZE / 2.0, 20);
        else
            g.strokeLine(4, CELL_SIZE / 2.0, 20, CELL_SIZE / 2.0);
    }

    /**
     * Alias del método {@code dibujarImpacto()}.
     *
     * @return El resultado de {@code dibujarImpacto()}.
     */
    public Canvas dibujarTocado() {
        return dibujarImpacto();
    }

}