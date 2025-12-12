package com.example.batallanaval.Views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GamePainter {

    private final int CELL_SIZE = 40;

    // --------------------------
    //  1. Barco (NECESARIO)
    // --------------------------
    public Canvas dibujarBarco(String tipo, int longitud, boolean horizontal) {

        int w = horizontal ? longitud * CELL_SIZE : CELL_SIZE;
        int h = horizontal ? CELL_SIZE : longitud * CELL_SIZE;

        Canvas canvas = new Canvas(w, h);
        GraphicsContext g = canvas.getGraphicsContext2D();

        // Dibujar rectángulo del barco
        g.setFill(Color.DARKGRAY);
        g.fillRect(0, 0, w, h);

        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        g.strokeRect(0, 0, w, h);

        return canvas;
    }

    // --------------------------
    //  2. Agua (NECESARIO)
    // --------------------------
    public Canvas dibujarAgua() {

        Canvas c = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext g = c.getGraphicsContext2D();

        g.setFill(Color.LIGHTBLUE);
        g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);

        // X de agua
        g.setStroke(Color.BLUE);
        g.setLineWidth(3);
        g.strokeLine(5, 5, CELL_SIZE - 5, CELL_SIZE - 5);
        g.strokeLine(CELL_SIZE - 5, 5, 5, CELL_SIZE - 5);

        return c;
    }

    // --------------------------
    //  3. Tocado (si lo usas)
    // --------------------------
    public Canvas dibujarTocado() {

        Canvas c = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext g = c.getGraphicsContext2D();

        g.setFill(Color.RED);
        g.fillOval(5, 5, CELL_SIZE - 10, CELL_SIZE - 10);

        return c;
    }

    // --------------------------
    //  4. Hundido (si lo usas)
    // --------------------------
    public Canvas dibujarHundido() {

        Canvas c = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext g = c.getGraphicsContext2D();

        g.setFill(Color.BLACK);
        g.fillRect(0, 0, CELL_SIZE, CELL_SIZE);

        return c;
    }
}
