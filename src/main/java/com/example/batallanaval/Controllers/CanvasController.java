package com.example.batallanaval.Controllers;


import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class CanvasController {

    private static final int cellSize =40;

    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private GridPane Grid;



    public Canvas dibujarBarco(int longitud, boolean horizontal) {
        // Usar tamaño fijo de celda basado en el GridPane

        Canvas canvas;
        if (horizontal) {
            canvas = new Canvas(cellSize * longitud, cellSize);
        } else {
            canvas = new Canvas(cellSize, cellSize * longitud);
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.GRAY);
        gc.fillRoundRect(5, 5, canvas.getWidth() - 10, canvas.getHeight()-10, 10, 10);

        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(2);
        gc.strokeRoundRect(5, 5, canvas.getWidth() - 10, canvas.getHeight() - 10, 10, 10);

        return canvas;
    }
    @FXML
    public void initialize() {

        // Ejecutar después de que se renderice la ventana
        javafx.application.Platform.runLater(() -> {

            Canvas barco1 = dibujarBarco(3,false);
            barco1.setLayoutX(0 * cellSize);
            barco1.setLayoutY(0 * cellSize);
            AnchorPane.getChildren().add(barco1);

            Canvas barco2 = dibujarBarco(2,true);
            barco1.setLayoutX(1 * cellSize);
            barco1.setLayoutY(3 * cellSize);
            AnchorPane.getChildren().add(barco2);



        });
    }

}
