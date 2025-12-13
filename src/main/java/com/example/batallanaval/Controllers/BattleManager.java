package com.example.batallanaval.Controllers;

import com.example.batallanaval.Models.*; // Importa GamePersistence de aquí
import com.example.batallanaval.Views.GamePainter;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.function.BiConsumer;

/**
 * Clase controladora que gestiona la lógica central de la fase de batalla del juego.
 * Controla el flujo de turnos (Humano vs IA), el procesamiento de disparos,
 * la actualización visual de los tableros y la gestión del fin de partida y la persistencia.
 */
public class BattleManager {

    private final Tablero tableroJugador;
    private final MachineAI machineAI;
    private final GamePainter painter;
    private final GridPane gridJugador;
    private final GridPane gridEnemigo;
    private final String nickname;
    private final BiConsumer<String, String> onGameOver;

    /** Instancia de la clase que maneja el guardado y la carga de partidas. */
    private final GamePersistence persistence;

    private boolean turnoHumano;
    private boolean juegoActivo;
    private boolean modoRevelar;

    /**
     * Constructor para inicializar el gestor de batalla.
     *
     * @param tableroJugador El tablero del jugador humano.
     * @param machineAI La instancia de la IA que contiene su tablero y lógica de ataque.
     * @param painter El objeto que maneja el dibujo de elementos gráficos.
     * @param gridJugador El {@code GridPane} de la vista del tablero del jugador (propio).
     * @param gridEnemigo El {@code GridPane} de la vista del tablero enemigo (para disparar).
     * @param nickname El nickname del jugador actual.
     * @param turnoHumano El estado inicial del turno.
     * @param juegoActivo Indica si la partida está en curso.
     * @param modoRevelar Indica el estado inicial del modo espía.
     * @param onGameOver Callback que se ejecuta al terminar el juego, pasando el título y mensaje de resultado.
     */
    public BattleManager(Tablero tableroJugador, MachineAI machineAI, GamePainter painter,
                         GridPane gridJugador, GridPane gridEnemigo,
                         String nickname, boolean turnoHumano, boolean juegoActivo, boolean modoRevelar,
                         BiConsumer<String, String> onGameOver) {
        this.tableroJugador = tableroJugador;
        this.machineAI = machineAI;
        this.painter = painter;
        this.gridJugador = gridJugador;
        this.gridEnemigo = gridEnemigo;
        this.nickname = nickname;
        this.turnoHumano = turnoHumano;
        this.juegoActivo = juegoActivo;
        this.modoRevelar = modoRevelar;
        this.onGameOver = onGameOver;
        this.persistence = new GamePersistence(); // Instancia del helper de Models
    }

    /**
     * Establece si el juego está activo o no.
     *
     * @param activo {@code true} para activar el juego, {@code false} para detenerlo.
     */
    public void setJuegoActivo(boolean activo) { this.juegoActivo = activo; }

    /**
     * Reanuda la partida después de una carga. Si no es turno humano, inicia el turno de la máquina.
     */
    public void reanudarPartida() {
        if (juegoActivo && !turnoHumano) iniciarTurnoMaquinaConHilo();
    }

    /**
     * Procesa un disparo realizado por el jugador humano.
     *
     * @param x Coordenada X (fila) del disparo.
     * @param y Coordenada Y (columna) del disparo.
     */
    public void manejarDisparoHumano(int x, int y) {
        if (!juegoActivo || !turnoHumano) return;

        int resultado = machineAI.getTablero().recibirDisparo(x, y);
        if (resultado == 3) return; // Disparo repetido o fuera de límites

        dibujarImpacto(gridEnemigo, x, y, resultado);
        guardarProgreso(); // Guarda el estado después del disparo

        if (machineAI.getTablero().estaDerrotado()) {
            terminarJuego("¡VICTORIA!", "¡FELICIDADES CAPITÁN!\nENEMIGO HUNDIDO.");
            return;
        }

        if (resultado == 0) { // Fallo (agua)
            turnoHumano = false;
            iniciarTurnoMaquinaConHilo(); // Pasa el turno a la IA
        }
    }

    /**
     * Inicia el turno de la máquina en un hilo separado con un retardo para simular el tiempo de pensamiento.
     */
    private void iniciarTurnoMaquinaConHilo() {
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            Platform.runLater(this::ejecutarDisparoIA); // Ejecuta el disparo en el hilo de la UI
        }).start();
    }

    /**
     * Ejecuta la lógica de ataque de la Inteligencia Artificial.
     */
    private void ejecutarDisparoIA() {
        if (!juegoActivo) return;

        // La IA decide dónde disparar
        int[] coords = machineAI.realizarDisparo(tableroJugador);
        int mx = coords[0]; int my = coords[1];
        int res = tableroJugador.recibirDisparo(mx, my);

        dibujarImpacto(gridJugador, mx, my, res);

        // Si fue un impacto (tocado o hundido), registra las coordenadas para la estrategia de caza
        if (res == 1 || res == 2) machineAI.registrarImpactoExitoso(mx, my);
        guardarProgreso();

        if (tableroJugador.estaDerrotado()) {
            terminarJuego("DERROTA", "LA MÁQUINA HA GANADO.\n¡RETIRADA!");
            return;
        }

        if (res == 0) turnoHumano = true; // Fallo (agua), pasa el turno al humano
        else iniciarTurnoMaquinaConHilo(); // Acierto (tocado/hundido), repite turno
    }

    /**
     * Alterna el estado del modo "Revelar Tablero Enemigo" (Modo Espía).
     * Se utiliza en la fase de colocación.
     */
    public void toggleRevelarTablero() {
        this.modoRevelar = !this.modoRevelar;
        repintarTableroEnemigo();
    }

    /**
     * Repinta el tablero enemigo (Grid2), mostrando las marcas de disparos y,
     * si el modo revelar está activo, la posición de los barcos enemigos.
     */
    public void repintarTableroEnemigo() {
        // Limpiar marcas anteriores (dejando solo la base)
        for (javafx.scene.Node node : gridEnemigo.getChildren()) {
            if (node instanceof Pane) ((Pane) node).getChildren().removeIf(n -> n instanceof Canvas);
        }

        Casilla[][] casillas = machineAI.getTablero().getCasillas();

        // 1. Mostrar Barcos (Modo Revelar / Trampa)
        if (modoRevelar) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (casillas[i][j].tieneBarco()) {
                        Pane c = obtenerCelda(gridEnemigo, i, j);
                        if (c!=null) agregarMarca(c, Color.rgb(50,50,50,0.5)); // Gris semi-transparente
                    }
                }
            }
        }

        // 2. Repintar todas las Marcas de Disparo (agua, tocado, hundido)
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (casillas[i][j].fueDisparada()) {
                    int res = casillas[i][j].tieneBarco() ? (casillas[i][j].getBarco().estaHundido()?2:1) : 0;
                    // Se usa dibujarImpacto para asegurar que las marcas quedan encima de la marca 'espía'
                    dibujarImpacto(gridEnemigo, i, j, res);
                }
            }
        }
    }

    /**
     * Dibuja la marca visual de impacto (agua, tocado o explosión) en las coordenadas del {@code GridPane} especificado.
     *
     * @param grid El {@code GridPane} donde se dibujará la marca.
     * @param x Coordenada X (fila).
     * @param y Coordenada Y (columna).
     * @param resultado El código de resultado del disparo (0=Agua, 1=Tocado, 2=Hundido).
     */
    private void dibujarImpacto(GridPane grid, int x, int y, int resultado) {
        Canvas marca = null;
        if(resultado==0) marca = painter.dibujarAgua();
        else if(resultado==1) marca = painter.dibujarTocado();
        else if(resultado==2) marca = painter.dibujarExplosion(); // Hundido

        if (marca != null) {
            marca.setMouseTransparent(true);
            grid.add(marca, x, y);
        }
    }

    /**
     * Agrega una simple marca de color a un {@code Pane} (celda). Utilizado para el modo revelar.
     *
     * @param p El {@code Pane} (celda) objetivo.
     * @param c El color a dibujar.
     */
    private void agregarMarca(Pane p, Color c) {
        Canvas cv = new Canvas(40,40);
        cv.getGraphicsContext2D().setFill(c);
        cv.getGraphicsContext2D().fillRect(2,2,36,36);
        cv.setMouseTransparent(true);
        p.getChildren().add(cv);
    }

    /**
     * Busca y retorna el {@code Pane} (celda) en un {@code GridPane} para las coordenadas dadas.
     *
     * @param g El {@code GridPane} donde buscar.
     * @param x Coordenada X (fila).
     * @param y Coordenada Y (columna).
     * @return El {@code Pane} encontrado o {@code null} si no se encuentra.
     */
    private Pane obtenerCelda(GridPane g, int x, int y) {
        for(javafx.scene.Node n : g.getChildren())
            if(GridPane.getColumnIndex(n)!=null && GridPane.getRowIndex(n)!=null && GridPane.getColumnIndex(n)==x && GridPane.getRowIndex(n)==y && n instanceof Pane) return (Pane)n;
        return null;
    }

    /**
     * Finaliza el juego, establece el estado a inactivo, guarda el progreso y ejecuta el callback de fin de juego.
     *
     * @param titulo El título del modal de fin de juego.
     * @param msg El mensaje de fin de juego.
     */
    private void terminarJuego(String titulo, String msg) {
        juegoActivo = false;
        guardarProgreso(); // Se guarda el estado final para el reporte
        onGameOver.accept(titulo, msg);
    }

    /**
     * Crea un {@code GameDTO} con el estado actual y utiliza {@code GamePersistence} para
     * serializar el juego y guardar el reporte del marcador.
     */
    private void guardarProgreso() {
        // Se establece juegoTerminado como true si el juego no está activo
        GameDTO dto = new GameDTO(tableroJugador, machineAI, turnoHumano, true, !juegoActivo, nickname, modoRevelar);

        // Uso de GamePersistence en Models
        persistence.guardarJuegoSerializado(dto, "batalla_naval_" + nickname + ".ser");
        persistence.guardarMarcadorTxt(nickname, contarBarcosHundidos(machineAI.getTablero()), juegoActivo, "marcador_" + nickname + ".txt");
    }

    /**
     * Cuenta el número de barcos hundidos en un tablero dado.
     *
     * @param tablero El {@code Tablero} donde contar los barcos.
     * @return El número de barcos únicos que están hundidos.
     */
    private int contarBarcosHundidos(Tablero tablero) {
        java.util.Set<com.example.batallanaval.Models.Barco> barcos = new java.util.HashSet<>();
        // Recorre todas las casillas para encontrar referencias únicas a los barcos
        for (Casilla[] f : tablero.getCasillas()) for (Casilla c : f) if (c.tieneBarco()) barcos.add(c.getBarco());

        int count = 0;
        // Cuenta cuántos de esos barcos únicos están hundidos
        for (var b : barcos) if (b.estaHundido()) count++;
        return count;
    }

    /**
     * Obtiene una colección de todos los objetos {@code Barco} únicos que pertenecen
     * al tablero de la IA. Utilizado por {@code CanvasController} para actualizar el panel de flota enemiga.
     *
     * @return Un {@code Set} de objetos {@code Barco} de la flota enemiga.
     */
    public java.util.Set<com.example.batallanaval.Models.Barco> getBarcosEnemigos() {
        java.util.Set<com.example.batallanaval.Models.Barco> barcos = new java.util.HashSet<>();
        Casilla[][] casillas = machineAI.getTablero().getCasillas();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (casillas[i][j].tieneBarco()) {
                    barcos.add(casillas[i][j].getBarco());
                }
            }
        }
        return barcos;
    }
}