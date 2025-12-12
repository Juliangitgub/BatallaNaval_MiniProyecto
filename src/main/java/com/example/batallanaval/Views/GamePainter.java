package com.example.batallanaval.Views;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class GamePainter {
    private static final int CELL_SIZE = 40;
    private final Map<String, Image> imagenesBarcos = new HashMap<>();

    public GamePainter() {
        cargarImagenes();
    }

    private void cargarImagenes() {
        try {
            // Asegúrate de crear la carpeta 'images' en resources y poner tus PNGs ahí
            imagenesBarcos.put("Portaaviones", new Image(getClass().getResourceAsStream("/com/example/batallanaval/images/portaaviones.png")));
            imagenesBarcos.put("Submarino",    new Image(getClass().getResourceAsStream("/com/example/batallanaval/images/submarino.png")));
            imagenesBarcos.put("Destructor",   new Image(getClass().getResourceAsStream("/com/example/batallanaval/images/destructor.png")));
            imagenesBarcos.put("Fragata",      new Image(getClass().getResourceAsStream("/com/example/batallanaval/images/fragata.png")));
        } catch (Exception e) {
            System.err.println("No se pudieron cargar las imágenes de los barcos. Verifica la carpeta resources/images.");
        }
    }

    public Canvas dibujarBarco(String tipo, int longitud, boolean horizontal) {
        double width = horizontal ? CELL_SIZE * longitud : CELL_SIZE;
        double height = horizontal ? CELL_SIZE : CELL_SIZE * longitud;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image img = imagenesBarcos.get(tipo);

        if (img != null) {
            if (horizontal) {
                gc.drawImage(img, 0, 0, width, height);
            } else {
                // Rotar la imagen 90 grados para vertical
                gc.save();
                gc.translate(width / 2, height / 2);
                gc.rotate(90);
                gc.drawImage(img, -height / 2, -width / 2, height, width);
                gc.restore();
            }
        } else {
            // Fallback: Rectángulo gris si no hay imagen
            gc.setFill(Color.GRAY);
            gc.fillRoundRect(2, 2, width - 4, height - 4, 10, 10);
            gc.setStroke(Color.BLACK);
            gc.strokeRoundRect(2, 2, width - 4, height - 4, 10, 10);
        }
        return canvas;
    }

    public Canvas dibujarAgua() {
        Canvas canvas = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeLine(10, 10, 30, 30);
        gc.strokeLine(30, 10, 10, 30);
        return canvas;
    }

    public Canvas dibujarTocado() {
        Canvas canvas = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(255, 69, 0, 0.8)); // Naranja fuego
        gc.fillOval(5, 5, 30, 30);
        gc.setFill(Color.YELLOW);
        gc.fillOval(12, 12, 16, 16);
        return canvas;
    }

    public Canvas dibujarHundido() {
        Canvas canvas = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(139, 0, 0, 0.8)); // Rojo sangre
        gc.fillRect(5, 5, 30, 30);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(5, 5, 35, 35);
        gc.strokeLine(35, 5, 5, 35);
        return canvas;
    }
}