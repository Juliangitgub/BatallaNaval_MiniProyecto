package com.example.batallanaval.Controllers;


import com.example.batallanaval.Models.BarcoModel;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class CanvasController {

    private ArrayList<Canvas>Barcos = new ArrayList<>();

    private static int cellSize =40;


    Image Cursor = new Image(
            getClass().getResourceAsStream("/com/example/batallanaval/CursorImage.png")
    );


    @FXML
    private VBox VboxLeft;

    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private GridPane Grid;

    @FXML
    private GridPane Grid2;


    @FXML
    public void initialize() {
        IniciarBarcos();
    }

    @FXML
    void OnMouseEnteredGrid2(MouseEvent event) {
        Grid2.setCursor(new ImageCursor(
                Cursor,
                Cursor.getWidth()/2,
                Cursor.getHeight()/2
        ));


    }

    public void IniciarBarcos(){

        for (int i=1; i<5; i++){
            Canvas canva = new BarcoModel(i,false).dibujarBarco();
            Barcos.add(canva);
        }

        for (Canvas canvas:Barcos){
            canvas.setCursor(javafx.scene.Cursor.HAND);
            canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->{
                System.out.println("Barco presionado Presionado");
            });
            VboxLeft.getChildren().add(canvas);
        }









    }



}
