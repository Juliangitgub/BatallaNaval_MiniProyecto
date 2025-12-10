package com.example.batallanaval.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartMenuView extends Stage {

    public StartMenuView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/batallanaval/Canvas.fxml")
        );
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        this.setTitle("Batalla Naval");
        this.setScene(scene);

        this.show();
    }

    public static StartMenuView getInstance() throws IOException {
        if (StartMenuViewHolder.INSTANCE == null){
            return StartMenuViewHolder.INSTANCE = new StartMenuView();
        }else {
            return StartMenuViewHolder.INSTANCE;
        }
    }

    private static class StartMenuViewHolder {
        private static StartMenuView INSTANCE;
    }
}

