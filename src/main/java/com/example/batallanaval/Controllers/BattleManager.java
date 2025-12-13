package com.example.batallanaval.Controllers;

import com.example.batallanaval.Models.*; // Importa GamePersistence de aquí
import com.example.batallanaval.Views.GamePainter;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.function.BiConsumer;

public class BattleManager {

    private final Tablero tableroJugador;
    private final MachineAI machineAI;
    private final GamePainter painter;
    private final GridPane gridJugador;
    private final GridPane gridEnemigo;
    private final String nickname;
    private final BiConsumer<String, String> onGameOver;

    // Ahora usa la clase de Models
    private final GamePersistence persistence;

    private boolean turnoHumano;
    private boolean juegoActivo;
    private boolean modoRevelar;

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

    public void setJuegoActivo(boolean activo) { this.juegoActivo = activo; }

    public void reanudarPartida() {
        if (juegoActivo && !turnoHumano) iniciarTurnoMaquinaConHilo();
    }

    public void manejarDisparoHumano(int x, int y) {
        if (!juegoActivo || !turnoHumano) return;

        int resultado = machineAI.getTablero().recibirDisparo(x, y);
        if (resultado == 3) return;

        dibujarImpacto(gridEnemigo, x, y, resultado);
        guardarProgreso();

        if (machineAI.getTablero().estaDerrotado()) {
            terminarJuego("¡VICTORIA!", "¡FELICIDADES CAPITÁN!\nENEMIGO HUNDIDO.");
            return;
        }

        if (resultado == 0) {
            turnoHumano = false;
            iniciarTurnoMaquinaConHilo();
        }
    }

    private void iniciarTurnoMaquinaConHilo() {
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            Platform.runLater(this::ejecutarDisparoIA);
        }).start();
    }

    private void ejecutarDisparoIA() {
        if (!juegoActivo) return;

        int[] coords = machineAI.realizarDisparo(tableroJugador);
        int mx = coords[0]; int my = coords[1];
        int res = tableroJugador.recibirDisparo(mx, my);

        dibujarImpacto(gridJugador, mx, my, res);
        if (res == 1 || res == 2) machineAI.registrarImpactoExitoso(mx, my);
        guardarProgreso();

        if (tableroJugador.estaDerrotado()) {
            terminarJuego("DERROTA", "LA MÁQUINA HA GANADO.\n¡RETIRADA!");
            return;
        }

        if (res == 0) turnoHumano = true;
        else iniciarTurnoMaquinaConHilo();
    }

    public void toggleRevelarTablero() {
        this.modoRevelar = !this.modoRevelar;
        repintarTableroEnemigo();
    }

    public void repintarTableroEnemigo() {
        for (javafx.scene.Node node : gridEnemigo.getChildren()) {
            if (node instanceof Pane) ((Pane) node).getChildren().removeIf(n -> n instanceof Canvas);
        }

        Casilla[][] casillas = machineAI.getTablero().getCasillas();
        // 1. Barcos (Trampa)
        if (modoRevelar) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (casillas[i][j].tieneBarco()) {
                        Pane c = obtenerCelda(gridEnemigo, i, j);
                        if (c!=null) agregarMarca(c, Color.rgb(50,50,50,0.5));
                    }
                }
            }
        }
        // 2. Disparos
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (casillas[i][j].fueDisparada()) {
                    int res = casillas[i][j].tieneBarco() ? (casillas[i][j].getBarco().estaHundido()?2:1) : 0;
                    dibujarImpacto(gridEnemigo, i, j, res);
                }
            }
        }
    }

    private void dibujarImpacto(GridPane grid, int x, int y, int resultado) {
        Canvas marca = null;
        if(resultado==0) marca = painter.dibujarAgua();
        else if(resultado==1) marca = painter.dibujarTocado();
        else marca = painter.dibujarExplosion();

        if (marca != null) {
            marca.setMouseTransparent(true);
            grid.add(marca, x, y);
        }
    }

    private void agregarMarca(Pane p, Color c) {
        Canvas cv = new Canvas(40,40);
        cv.getGraphicsContext2D().setFill(c);
        cv.getGraphicsContext2D().fillRect(2,2,36,36);
        cv.setMouseTransparent(true);
        p.getChildren().add(cv);
    }

    private Pane obtenerCelda(GridPane g, int x, int y) {
        for(javafx.scene.Node n : g.getChildren())
            if(GridPane.getColumnIndex(n)==x && GridPane.getRowIndex(n)==y && n instanceof Pane) return (Pane)n;
        return null;
    }

    private void terminarJuego(String titulo, String msg) {
        juegoActivo = false;
        guardarProgreso();
        onGameOver.accept(titulo, msg);
    }

    private void guardarProgreso() {
        GameDTO dto = new GameDTO(tableroJugador, machineAI, turnoHumano, true, !juegoActivo, nickname, modoRevelar);

        // Uso de GamePersistence en Models
        persistence.guardarJuegoSerializado(dto, "batalla_naval_" + nickname + ".ser");
        persistence.guardarMarcadorTxt(nickname, contarBarcosHundidos(machineAI.getTablero()), juegoActivo, "marcador_" + nickname + ".txt");
    }

    private int contarBarcosHundidos(Tablero tablero) {
        java.util.Set<com.example.batallanaval.Models.Barco> barcos = new java.util.HashSet<>();
        for (Casilla[] f : tablero.getCasillas()) for (Casilla c : f) if (c.tieneBarco()) barcos.add(c.getBarco());
        int count = 0;
        for (var b : barcos) if (b.estaHundido()) count++;
        return count;
    }
    // Método para obtener la lista de barcos únicos del enemigo
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