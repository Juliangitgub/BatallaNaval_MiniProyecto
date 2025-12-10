package com.example.batallanaval;

import com.example.batallanaval.Views.StartMenuView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        StartMenuView mainMenu = StartMenuView.getInstance();
    }

}
