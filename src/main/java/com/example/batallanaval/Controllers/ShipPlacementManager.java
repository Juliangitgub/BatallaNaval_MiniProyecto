package com.example.batallanaval.Controllers;

import com.example.batallanaval.Models.Barco;
import com.example.batallanaval.Models.BarcoFactory;
import com.example.batallanaval.Models.Tablero;
import com.example.batallanaval.Views.GamePainter;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Clase controladora encargada de gestionar la fase de colocación de barcos del jugador humano
 * en el {@code Tablero}. Maneja la lógica de arrastrar y soltar (Drag and Drop) y la rotación de los barcos.
 */
public class ShipPlacementManager {

    private final Tablero tableroJugador;
    private final GamePainter painter;
    private final VBox vboxBarcos;
    private final Runnable onFlotaListaCallback;

    /**
     * Constructor para inicializar el gestor de colocación de barcos.
     *
     * @param tablero El {@code Tablero} del jugador donde se colocarán los barcos.
     * @param painter La instancia de {@code GamePainter} utilizada para dibujar los barcos.
     * @param vboxBarcos El {@code VBox} en el sidebar que contiene la lista de barcos a colocar.
     * @param onFlotaLista La acción a ejecutar (callback) una vez que todos los barcos hayan sido colocados.
     */
    public ShipPlacementManager(Tablero tablero, GamePainter painter, VBox vboxBarcos, Runnable onFlotaLista) {
        this.tableroJugador = tablero;
        this.painter = painter;
        this.vboxBarcos = vboxBarcos;
        this.onFlotaListaCallback = onFlotaLista;
    }

    /**
     * Configura los controladores de eventos de Drag and Drop (arrastrar y soltar)
     * en todas las celdas del {@code GridPane} que representa el tablero del jugador.
     *
     * @param grid El {@code GridPane} del tablero del jugador.
     */
    public void configurarTableroParaSoltar(GridPane grid) {
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof Pane) {
                Pane celda = (Pane) node;
                Integer x = GridPane.getColumnIndex(node);
                Integer y = GridPane.getRowIndex(node);

                if (x != null && y != null) {
                    celda.setOnDragOver(this::handleDragOver);
                    celda.setOnDragDropped(e -> handleDragDropped(e, x, y, grid));
                }
            }
        }
    }

    /**
     * Carga la lista completa de barcos necesarios en el {@code VBox} del sidebar,
     * configurándolos para que sean arrastrables.
     */
    public void cargarBarcosEnSidebar() {
        vboxBarcos.getChildren().clear();
        vboxBarcos.setSpacing(10);
        vboxBarcos.setAlignment(Pos.CENTER);

        // --- ETIQUETA DE INSTRUCCIONES ---
        Label lblInstruccion = new Label("🖱 Clic Derecho\n  para ROTAR ↻");
        lblInstruccion.setTextFill(Color.WHITE);
        lblInstruccion.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblInstruccion.setStyle("-fx-border-color: white; -fx-padding: 5; -fx-background-color: rgba(0,0,0,0.3);");
        lblInstruccion.setAlignment(Pos.CENTER);
        vboxBarcos.getChildren().add(lblInstruccion);
        // ---------------------------------

        crearBarcoDraggable("Portaaviones", 4);
        crearBarcoDraggable("Submarino", 3);
        crearBarcoDraggable("Submarino", 3);
        crearBarcoDraggable("Destructor", 2);
        crearBarcoDraggable("Destructor", 2);
        crearBarcoDraggable("Destructor", 2);
        crearBarcoDraggable("Fragata", 1);
        crearBarcoDraggable("Fragata", 1);
        crearBarcoDraggable("Fragata", 1);
        crearBarcoDraggable("Fragata", 1);
    }

    /**
     * Crea la representación visual de un barco ({@code Canvas}) y le añade la lógica
     * para arrastrar y rotar (clic derecho).
     *
     * @param tipo El tipo de barco (ej. "Portaaviones").
     * @param longitud La longitud del barco.
     */
    private void crearBarcoDraggable(String tipo, int longitud) {
        final boolean[] esHorizontal = {true};

        Canvas view = painter.dibujarBarco(tipo, longitud, true);
        Tooltip.install(view, new Tooltip("Arrastra al tablero\nClic derecho para girar"));

        // Evento ROTAR (Clic Derecho)
        view.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                esHorizontal[0] = !esHorizontal[0];
                Canvas nuevoDibujo = painter.dibujarBarco(tipo, longitud, esHorizontal[0]);

                int index = vboxBarcos.getChildren().indexOf(view);
                if (index != -1) {
                    vboxBarcos.getChildren().set(index, nuevoDibujo);
                    // Reasignamos eventos al nuevo nodo recursivamente
                    configurarEventosDrag(nuevoDibujo, tipo, longitud, esHorizontal);
                }
            }
        });

        configurarEventosDrag(view, tipo, longitud, esHorizontal);
        vboxBarcos.getChildren().add(view);
    }

    /**
     * Configura los eventos de arrastre (Drag Detected) y rotación para un {@code Canvas} de barco.
     * Al detectar el arrastre, se codifica el tipo, longitud y orientación en el {@code Dragboard}.
     *
     * @param view El {@code Canvas} que representa el barco.
     * @param tipo El tipo de barco.
     * @param longitud La longitud del barco.
     * @param esHorizontal Arreglo booleano que mantiene la orientación actual.
     */
    private void configurarEventosDrag(Canvas view, String tipo, int longitud, boolean[] esHorizontal) {
        // Reasignamos clic derecho por si se regeneró la vista
        view.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                esHorizontal[0] = !esHorizontal[0];
                Canvas nuevoDibujo = painter.dibujarBarco(tipo, longitud, esHorizontal[0]);
                int index = vboxBarcos.getChildren().indexOf(view);
                if (index != -1) {
                    vboxBarcos.getChildren().set(index, nuevoDibujo);
                    configurarEventosDrag(nuevoDibujo, tipo, longitud, esHorizontal);
                }
            }
        });

        view.setOnDragDetected(e -> {
            Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            // Guardamos la rotación en el String: TIPO,LONGITUD,HORIZONTAL
            content.putString(tipo + "," + longitud + "," + esHorizontal[0]);
            db.setContent(content);

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            WritableImage snapshot = view.snapshot(params, null);
            db.setDragView(snapshot);

            view.setUserData("DRAGGED"); // Marca el nodo que está siendo arrastrado
            e.consume();
        });
    }

    /**
     * Maneja el evento cuando un objeto arrastrado pasa sobre una celda del tablero.
     * Acepta la transferencia si el objeto arrastrado contiene datos de tipo String.
     *
     * @param event El evento de arrastre.
     */
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    /**
     * Maneja el evento cuando un objeto arrastrado es soltado en una celda del tablero.
     * Intenta colocar el barco en el {@code Tablero} del jugador.
     *
     * @param event El evento de soltar.
     * @param x Coordenada X (fila) de la celda donde se soltó el barco.
     * @param y Coordenada Y (columna) de la celda donde se soltó el barco.
     * @param grid El {@code GridPane} del tablero para añadir la representación visual.
     */
    private void handleDragDropped(DragEvent event, int x, int y, GridPane grid) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasString()) {
            String[] data = db.getString().split(",");
            String tipo = data[0];
            int longitud = Integer.parseInt(data[1]);
            boolean horizontal = Boolean.parseBoolean(data[2]);

            try {
                // 1. Colocación en el modelo
                Barco barco = BarcoFactory.crearBarco(tipo);
                tableroJugador.colocarBarco(barco, x, y, horizontal);

                // 2. Colocación visual en la vista
                Canvas barcoView = painter.dibujarBarco(tipo, longitud, horizontal);
                barcoView.setMouseTransparent(true);
                grid.add(barcoView, x, y);
                if (horizontal) GridPane.setColumnSpan(barcoView, longitud);
                else GridPane.setRowSpan(barcoView, longitud);

                // 3. Remover de la lista de pendientes (sidebar)
                vboxBarcos.getChildren().removeIf(n -> "DRAGGED".equals(n.getUserData()));
                success = true;

                // 4. Verificar fin de colocación
                // Se verifica si quedan Canvas (ignorando el Label de instrucciones)
                boolean quedanBarcos = vboxBarcos.getChildren().stream().anyMatch(n -> n instanceof Canvas);

                if (!quedanBarcos) {
                    vboxBarcos.getChildren().clear(); // Quitamos el Label también
                    onFlotaListaCallback.run(); // Ejecuta el callback para iniciar la batalla
                }

            } catch (Exception e) {
                // Posición inválida (MovimientoInvalidoException capturada)
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }
}