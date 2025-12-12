package com.example.batallanaval.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CanvasView extends Stage {

    public CanvasView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/batallanaval/Canvas.fxml")
        );
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        this.setTitle("Batalla Naval");
        this.setScene(scene);

        this.show();
    }

    public static CanvasView getInstance() throws IOException {
        if (StartMenuViewHolder.INSTANCE == null){
            return StartMenuViewHolder.INSTANCE = new CanvasView();
        }else {
            return StartMenuViewHolder.INSTANCE;
        }
    }

    private static class StartMenuViewHolder {
        private static CanvasView INSTANCE;
    }
}

