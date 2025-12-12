package com.example.batallanaval.Controllers;

import com.example.batallanaval.Models.*; // Aquí está GamePersistence
import com.example.batallanaval.Views.GamePainter;
import com.example.batallanaval.Views.ModalFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CanvasController {

    @FXML private StackPane rootPane;
    @FXML private VBox VboxLeft;
    @FXML private GridPane Grid;
    @FXML private GridPane Grid2;
    @FXML private VBox VboxRight;

    private ShipPlacementManager placementManager;
    private BattleManager battleManager;

    //GamePersistence de Models ---
    private GamePersistence persistence;

    private ModalFactory modalFactory;
    private GamePainter gamePainter;

    private boolean faseDeBatalla = false;
    private String nickname;

    @FXML
    public void initialize() {
        // CORRECCIÓN: Instanciamos la clase correcta
        this.persistence = new GamePersistence();

        this.modalFactory = new ModalFactory(rootPane);
        this.gamePainter = new GamePainter();

        Platform.runLater(() -> modalFactory.mostrarInputNickname(nombre -> {
            this.nickname = nombre;
            gestionarInicio();
        }));

        VboxLeft.sceneProperty().addListener((obs, o, n) -> { if(n!=null) n.setOnKeyPressed(this::manejarTeclado); });
    }

    private void gestionarInicio() {
        String archivo = "batalla_naval_" + nickname + ".ser";

        // Método correcto de GamePersistence
        if (persistence.existePartida(nickname)) {
            GameDTO estado = persistence.cargarJuego(archivo);
            if (estado != null && !estado.isJuegoTerminado()) {
                modalFactory.mostrarConfirmacion(
                        "PARTIDA ENCONTRADA",
                        "Hola " + nickname + ", ¿continuar partida?",
                        "Continuar", "Nueva",
                        () -> cargarPartida(estado),
                        this::iniciarNuevoJuego
                );
                return;
            }
        }
        iniciarNuevoJuego();
    }

    private void iniciarNuevoJuego() {
        limpiarUI();
        faseDeBatalla = false;

        Tablero t = new Tablero();
        MachineAI ai = new MachineAI();

        this.placementManager = new ShipPlacementManager(t, gamePainter, VboxLeft, this::activarFaseBatalla);
        this.battleManager = new BattleManager(t, ai, gamePainter, Grid, Grid2, nickname, true, false, false,
                (titulo, msg) -> modalFactory.mostrarModalFinJuego(titulo, msg, this::iniciarNuevoJuego, Platform::exit)
        );

        inicializarCeldas(Grid, true);
        inicializarCeldas(Grid2, false);
        placementManager.cargarBarcosEnSidebar();

        actualizarPanelEnemigo();

        configurarCursor();

        modalFactory.mostrarConfirmacion("MODO ESPÍA", "¿Activar modo espía? (Tecla 'T')", "Sí", "No",
                () -> battleManager.toggleRevelarTablero(), () -> {});
    }

    private void cargarPartida(GameDTO estado) {
        limpiarUI();
        this.faseDeBatalla = true;

        this.battleManager = new BattleManager(estado.getTableroJugador(), estado.getMachineAI(), gamePainter, Grid, Grid2,
                nickname, estado.isTurnoHumano(), true, estado.isModoRevelar(),
                (titulo, msg) -> modalFactory.mostrarModalFinJuego(titulo, msg, this::iniciarNuevoJuego, Platform::exit)
        );
        this.placementManager = null;

        inicializarCeldas(Grid, true);
        inicializarCeldas(Grid2, false);
        repintarTableroPropio(Grid, estado.getTableroJugador(), gamePainter);
        battleManager.repintarTableroEnemigo(); // Esto repinta también los disparos

        actualizarPanelEnemigo();

        configurarCursor();
        modalFactory.mostrarAlerta("CARGADO", "Turno: " + (estado.isTurnoHumano() ? "Tuyo" : "IA"));
        battleManager.reanudarPartida();
    }

    private void actualizarPanelEnemigo() {
        if (battleManager == null) return;

        VboxRight.getChildren().clear();

        Label titulo = new Label("FLOTA ENEMIGA");
        titulo.setTextFill(Color.web("#00cdc9"));
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        VboxRight.getChildren().add(titulo);

        // Usamos la lista de BattleManager (requiere el getter que añadimos antes)
        // IMPORTANTE: Asegúrate de haber pegado el getter en BattleManager.java como te dije antes
        // Si sale error aquí, es porque falta getBarcosEnemigos() en BattleManager.
        List<Barco> flota = new ArrayList<>(battleManager.getBarcosEnemigos());
        flota.sort(Comparator.comparingInt(Barco::getLongitud).reversed());

        for (Barco barco : flota) {
            Canvas barcoView = gamePainter.dibujarBarco(barco.getTipo(), barco.getLongitud(), true);

            if (barco.estaHundido()) {
                barcoView.setOpacity(0.5);
                var gc = barcoView.getGraphicsContext2D();
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.strokeLine(0, 0, barcoView.getWidth(), barcoView.getHeight());
                gc.strokeLine(barcoView.getWidth(), 0, 0, barcoView.getHeight());
            }

            VboxRight.getChildren().add(barcoView);
        }
    }

    private void activarFaseBatalla() {
        this.faseDeBatalla = true;
        battleManager.setJuegoActivo(true);
        modalFactory.mostrarAlerta("LISTO", "¡A luchar!");
        actualizarPanelEnemigo();
    }

    private void inicializarCeldas(GridPane grid, boolean esPropio) {
        for(int i=0; i<10; i++) {
            for(int j=0; j<10; j++) {
                Pane p = new Pane(); p.setPrefSize(40,40); p.setStyle("-fx-border-color: #008b8b;");
                int x=i; int y=j;
                if(esPropio) { if(placementManager!=null) placementManager.configurarTableroParaSoltar(grid); }
                else {
                    p.setOnMouseClicked(e -> {
                        if(faseDeBatalla) {
                            battleManager.manejarDisparoHumano(x,y);
                            actualizarPanelEnemigo();
                        }
                        else modalFactory.mostrarAlerta("ESPERA", "Coloca barcos.");
                    });
                }
                grid.add(p,x,y);
            }
        }
    }

    private void repintarTableroPropio(GridPane grid, Tablero tablero, GamePainter painter) {
        Casilla[][] casillas = tablero.getCasillas();
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                Casilla c = casillas[i][j];
                if (c.tieneBarco()) {
                    Barco b = c.getBarco();
                    boolean esCabeza = (b.isHorizontal() && (i==0 || casillas[i-1][j].getBarco()!=b)) ||
                            (!b.isHorizontal() && (j==0 || casillas[i][j-1].getBarco()!=b));
                    if (esCabeza) {
                        Canvas view = painter.dibujarBarco(b.getTipo(), b.getLongitud(), b.isHorizontal());
                        view.setMouseTransparent(true);
                        grid.add(view, i, j);
                        if(b.isHorizontal()) GridPane.setColumnSpan(view, b.getLongitud());
                        else GridPane.setRowSpan(view, b.getLongitud());
                    }
                }
                if (c.fueDisparada()) {
                    int res = c.tieneBarco() ? (c.getBarco().estaHundido()?2:1) : 0;
                    Canvas m = (res==0) ? painter.dibujarAgua() : (res==1 ? painter.dibujarTocado() : painter.dibujarHundido());
                    if(m!=null) { m.setMouseTransparent(true); grid.add(m, i, j); }
                }
            }
        }
    }

    private void manejarTeclado(KeyEvent event) {
        if (event.getCode() == KeyCode.T) {
            if (!faseDeBatalla) {
                battleManager.toggleRevelarTablero();
                modalFactory.mostrarAlerta("ESPÍA", "Visibilidad alternada.");
            } else modalFactory.mostrarAlerta("ERROR", "Modo espía bloqueado.");
        }
    }

    private void limpiarUI() { Grid.getChildren().clear(); Grid2.getChildren().clear(); VboxLeft.getChildren().clear(); VboxRight.getChildren().clear(); }

    private void configurarCursor() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/com/example/batallanaval/cursor.png"));
            ImageCursor c = new ImageCursor(img, img.getWidth()/2, img.getHeight()/2);
            Grid2.setOnMouseEntered(e -> Grid2.getScene().setCursor(c));
            Grid2.setOnMouseExited(e -> Grid2.getScene().setCursor(javafx.scene.Cursor.DEFAULT));
        } catch (Exception e) {}
    }
    @FXML public void OnMouseEnteredGrid2(MouseEvent event) {}
}