package com.example.batallanaval.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase que representa la vista principal del juego (el Canvas o tablero)
 * y extiende {@code Stage}, actuando como la ventana principal.
 * Esta clase implementa el patrón Singleton para asegurar que solo exista
 * una instancia de la ventana de juego.
 */
public class CanvasView extends Stage {

    /**
     * Constructor para inicializar la vista de la aplicación.
     * Carga el archivo FXML, establece la escena y muestra la ventana.
     *
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
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

    /**
     * Obtiene la única instancia de la clase {@code CanvasView}, siguiendo el patrón Singleton.
     * Si la instancia no existe, la crea.
     *
     * @return La instancia única de {@code CanvasView}.
     * @throws IOException Si ocurre un error al cargar la vista al crear la nueva instancia.
     */
    public static CanvasView getInstance() throws IOException {
        if (StartMenuViewHolder.INSTANCE == null){
            return StartMenuViewHolder.INSTANCE = new CanvasView();
        }else {
            return StartMenuViewHolder.INSTANCE;
        }
    }

    /**
     * Clase interna estática para mantener la instancia única de {@code CanvasView} (Lazy Initialization Singleton).
     */
    private static class StartMenuViewHolder {
        private static CanvasView INSTANCE;
    }
}