package com.example.batallanaval.Models;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BarcoModel {
    private int lenght;
    private boolean horizontal;


    public BarcoModel(int longitud, boolean horizontal){
        this.lenght = longitud;
        this.horizontal = horizontal;
    }

    public Canvas dibujarBarco() {
        // Usar tamaño fijo de celda basado en el GridPane

        Canvas canvas;
        if (horizontal) {
            canvas = new Canvas(40 * lenght, 40);
        } else {
            canvas = new Canvas(40, 40 * lenght);
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.GRAY);
        gc.fillRoundRect(5, 5, canvas.getWidth() - 10, canvas.getHeight()-10, 10, 10);

        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(2);
        gc.strokeRoundRect(5, 5, canvas.getWidth() - 10, canvas.getHeight() - 10, 10, 10);

        return canvas;
    }


}
