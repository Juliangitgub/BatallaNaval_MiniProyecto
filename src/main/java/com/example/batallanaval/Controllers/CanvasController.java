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

/**
 * Controlador principal de la vista Canvas (tablero de juego).
 * Gestiona la inicialización, la lógica de inicio/carga de partida,
 * la coordinación entre {@code ShipPlacementManager} y {@code BattleManager},
 * y maneja las interacciones de UI (clics, teclado, modales).
 */
public class CanvasController {

    @FXML private StackPane rootPane;
    @FXML private VBox VboxLeft;
    @FXML private GridPane Grid;
    @FXML private GridPane Grid2;
    @FXML private VBox VboxRight;

    private ShipPlacementManager placementManager;
    private BattleManager battleManager;

    /** Instancia para manejar la persistencia del juego (guardar/cargar). */
    private GamePersistence persistence;

    private ModalFactory modalFactory;
    private GamePainter gamePainter;

    private boolean faseDeBatalla = false;
    private String nickname;

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Instancia servicios y solicita el nickname del jugador para gestionar el inicio de la partida.
     */
    @FXML
    public void initialize() {
        // CORRECCIÓN: Instanciamos la clase correcta
        this.persistence = new GamePersistence();

        this.modalFactory = new ModalFactory(rootPane);
        this.gamePainter = new GamePainter();

        // Solicita el nickname al inicio y luego llama a gestionarInicio
        Platform.runLater(() -> modalFactory.mostrarInputNickname(nombre -> {
            this.nickname = nombre;
            gestionarInicio();
        }));

        // Listener para capturar eventos de teclado, como el modo espía (tecla 'T')
        VboxLeft.sceneProperty().addListener((obs, o, n) -> { if(n!=null) n.setOnKeyPressed(this::manejarTeclado); });
    }

    /**
     * Gestiona la lógica de inicio del juego: verifica si existe una partida guardada
     * para el nickname actual y ofrece al usuario la opción de cargarla o iniciar un nuevo juego.
     */
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

    /**
     * Prepara el entorno para una nueva partida: inicializa el tablero, la IA,
     * el {@code ShipPlacementManager} y la UI para la fase de colocación.
     */
    private void iniciarNuevoJuego() {
        limpiarUI();
        faseDeBatalla = false;

        Tablero t = new Tablero();
        MachineAI ai = new MachineAI();

        // Inicializa el gestor de colocación, pasándole el callback para activar la batalla
        this.placementManager = new ShipPlacementManager(t, gamePainter, VboxLeft, this::activarFaseBatalla);
        // Inicializa el BattleManager
        this.battleManager = new BattleManager(t, ai, gamePainter, Grid, Grid2, nickname, true, false, false,
                (titulo, msg) -> modalFactory.mostrarModalFinJuego(titulo, msg, this::iniciarNuevoJuego, Platform::exit)
        );

        inicializarCeldas(Grid, true);
        inicializarCeldas(Grid2, false);
        placementManager.cargarBarcosEnSidebar();

        // El panel de la derecha se actualiza al inicio, mostrando los barcos por hundir
        actualizarPanelEnemigo();

        configurarCursor();

        modalFactory.mostrarConfirmacion("MODO ESPÍA", "¿Activar modo espía? (Tecla 'T')", "Sí", "No",
                () -> battleManager.toggleRevelarTablero(), () -> {});
    }

    /**
     * Carga el estado del juego desde un {@code GameDTO} serializado.
     * Restaura los tableros, el estado del juego y el turno.
     *
     * @param estado El {@code GameDTO} cargado desde el archivo.
     */
    private void cargarPartida(GameDTO estado) {
        limpiarUI();
        this.faseDeBatalla = true;

        // Recrea el BattleManager con los estados guardados
        this.battleManager = new BattleManager(estado.getTableroJugador(), estado.getMachineAI(), gamePainter, Grid, Grid2,
                nickname, estado.isTurnoHumano(), true, estado.isModoRevelar(),
                (titulo, msg) -> modalFactory.mostrarModalFinJuego(titulo, msg, this::iniciarNuevoJuego, Platform::exit)
        );
        this.placementManager = null; // La fase de colocación ya terminó

        inicializarCeldas(Grid, true);
        inicializarCeldas(Grid2, false);
        repintarTableroPropio(Grid, estado.getTableroJugador(), gamePainter);
        battleManager.repintarTableroEnemigo(); // Esto repinta también los disparos

        actualizarPanelEnemigo();

        configurarCursor();
        modalFactory.mostrarAlerta("CARGADO", "Turno: " + (estado.isTurnoHumano() ? "Tuyo" : "IA"));
        battleManager.reanudarPartida();
    }

    /**
     * Actualiza el panel de la derecha (VboxRight) con el estado visual de la flota enemiga.
     * Muestra los barcos con su respectiva opacidad y líneas de hundimiento si han sido destruidos.
     */
    private void actualizarPanelEnemigo() {
        if (battleManager == null) return;

        VboxRight.getChildren().clear();

        Label titulo = new Label("FLOTA ENEMIGA");
        titulo.setTextFill(Color.web("#00cdc9"));
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        VboxRight.getChildren().add(titulo);

        // Obtiene la lista de barcos del AI y los ordena por longitud (descendente)
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

    /**
     * Callback que se ejecuta cuando el jugador ha terminado de colocar todos sus barcos.
     * Transiciona la UI a la fase de batalla.
     */
    private void activarFaseBatalla() {
        this.faseDeBatalla = true;
        battleManager.setJuegoActivo(true);
        modalFactory.mostrarAlerta("LISTO", "¡A luchar!");
        actualizarPanelEnemigo();
    }

    /**
     * Inicializa las celdas del {@code GridPane} creando los {@code Pane} contenedores
     * y asignando los listeners de eventos (Drag and Drop o Click de Disparo).
     *
     * @param grid El {@code GridPane} a inicializar (Grid para el propio, Grid2 para el enemigo).
     * @param esPropio {@code true} si es el tablero del jugador (requiere D&D), {@code false} para el tablero enemigo (requiere clicks).
     */
    private void inicializarCeldas(GridPane grid, boolean esPropio) {
        for(int i=0; i<10; i++) {
            for(int j=0; j<10; j++) {
                Pane p = new Pane(); p.setPrefSize(40,40); p.setStyle("-fx-border-color: #008b8b;");
                int x=i; int y=j;
                if(esPropio) {
                    // Si es propio, configuramos D&D
                    if(placementManager!=null) placementManager.configurarTableroParaSoltar(grid);
                }
                else {
                    // Si es enemigo, configuramos click para disparar
                    p.setOnMouseClicked(e -> {
                        if(faseDeBatalla) {
                            battleManager.manejarDisparoHumano(x,y);
                            actualizarPanelEnemigo(); // Se actualiza para mostrar si se hundió un barco
                        }
                        else modalFactory.mostrarAlerta("ESPERA", "Coloca barcos.");
                    });
                }
                grid.add(p,x,y);
            }
        }
    }

    /**
     * Repinta visualmente el tablero del jugador humano, mostrando la posición de sus barcos
     * y las marcas de los disparos recibidos (agua, tocado, hundido).
     *
     * @param grid El {@code GridPane} del tablero del jugador.
     * @param tablero El {@code Tablero} del jugador con el estado de las casillas.
     * @param painter La instancia de {@code GamePainter} para dibujar los elementos.
     */
    private void repintarTableroPropio(GridPane grid, Tablero tablero, GamePainter painter) {
        Casilla[][] casillas = tablero.getCasillas();
        for (int i=0; i<10; i++) {
            for (int j=0; j<10; j++) {
                Casilla c = casillas[i][j];

                // Dibuja el barco si está en la casilla (solo la cabeza para evitar duplicados)
                if (c.tieneBarco()) {
                    Barco b = c.getBarco();
                    // Lógica para detectar el inicio (cabeza) del barco
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

                // Dibuja la marca del disparo recibido
                if (c.fueDisparada()) {
                    int res = c.tieneBarco() ? (c.getBarco().estaHundido()?2:1) : 0;
                    Canvas m = (res==0) ? painter.dibujarAgua() : (res==1 ? painter.dibujarTocado() : painter.dibujarExplosion());
                    if(m!=null) { m.setMouseTransparent(true); grid.add(m, i, j); }
                }
            }
        }
    }

    /**
     * Maneja eventos de teclado. Actualmente se usa para el control del "Modo Espía" (tecla 'T').
     *
     * @param event El evento de teclado.
     */
    private void manejarTeclado(KeyEvent event) {
        if (event.getCode() == KeyCode.T) {
            // Permitir alternar el modo solo si NO ha iniciado la fase de batalla
            if (!faseDeBatalla) {
                battleManager.toggleRevelarTablero();
                modalFactory.mostrarAlerta("ESPÍA", "Visibilidad alternada.");
            } else modalFactory.mostrarAlerta("ERROR", "Modo espía bloqueado.");
        }
    }

    /**
     * Limpia todos los elementos visuales de los {@code GridPane} y {@code VBox} de la interfaz.
     */
    private void limpiarUI() {
        Grid.getChildren().clear();
        Grid2.getChildren().clear();
        VboxLeft.getChildren().clear();
        VboxRight.getChildren().clear();
    }

    /**
     * Configura un cursor personalizado para el tablero enemigo (Grid2) cuando el ratón
     * entra en su área, y restaura el cursor por defecto al salir.
     */
    private void configurarCursor() {
        try {
            // Asume que 'cursor.png' existe en el path de recursos
            Image img = new Image(getClass().getResourceAsStream("/com/example/batallanaval/cursor.png"));
            ImageCursor c = new ImageCursor(img, img.getWidth()/2, img.getHeight()/2);
            Grid2.setOnMouseEntered(e -> Grid2.getScene().setCursor(c));
            Grid2.setOnMouseExited(e -> Grid2.getScene().setCursor(javafx.scene.Cursor.DEFAULT));
        } catch (Exception e) {
            // Ignorar si el recurso del cursor no se encuentra
        }
    }

    /**
     * Controlador de evento FXML para la entrada del ratón en Grid2.
     * Este método FXML solo sirve como punto de entrada de JavaFX, la lógica
     * del cursor está en {@code configurarCursor()}.
     *
     * @param event El evento de ratón.
     */
    @FXML public void OnMouseEnteredGrid2(MouseEvent event) {}
}