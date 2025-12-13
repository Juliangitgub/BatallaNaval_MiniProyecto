package com.example.batallanaval.Models;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) que encapsula el estado completo del juego Batalla Naval.
 * Este objeto es serializable y se utiliza para guardar y cargar partidas.
 */
public class GameDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Tablero del jugador humano, con la posición de sus barcos. */
    private Tablero tableroJugador;
    /** Objeto que gestiona el tablero y la lógica de ataque de la IA (máquina). */
    private MachineAI machineAI;
    /** Indica si es el turno del jugador humano (true) o de la máquina (false). */
    private boolean turnoHumano;
    /** Indica si el juego ha pasado la fase de colocación de barcos e inició la batalla (true). */
    private boolean faseDeBatalla;
    /** Indica si la partida ha finalizado (true). */
    private boolean juegoTerminado;
    /** Nickname del jugador humano. */
    private String nickname;
    /** Bandera que indica si el modo de revelar (mostrar barcos enemigos) está activo (true). */
    private boolean modoRevelar; // <--- NUEVO CAMPO

    /**
     * Constructor que inicializa todos los parámetros necesarios para guardar el estado del juego.
     *
     * @param tableroJugador El tablero del jugador humano.
     * @param machineAI La instancia de la IA.
     * @param turnoHumano El estado del turno actual (true si es turno humano).
     * @param faseDeBatalla El estado de la fase del juego (true si están en batalla).
     * @param juegoTerminado El estado de terminación del juego.
     * @param nickname El nickname del jugador.
     * @param modoRevelar El estado del modo revelar.
     */
    public GameDTO(Tablero tableroJugador, MachineAI machineAI, boolean turnoHumano,
                   boolean faseDeBatalla, boolean juegoTerminado, String nickname, boolean modoRevelar) {
        this.tableroJugador = tableroJugador;
        this.machineAI = machineAI;
        this.turnoHumano = turnoHumano;
        this.faseDeBatalla = faseDeBatalla;
        this.juegoTerminado = juegoTerminado;
        this.nickname = nickname;
        this.modoRevelar = modoRevelar; // <--- GUARDARLO
    }

    // Getters
    /**
     * Obtiene el tablero del jugador humano.
     *
     * @return El objeto {@code Tablero} del jugador.
     */
    public Tablero getTableroJugador() { return tableroJugador; }

    /**
     * Obtiene la instancia de la Inteligencia Artificial.
     *
     * @return El objeto {@code MachineAI}.
     */
    public MachineAI getMachineAI() { return machineAI; }

    /**
     * Verifica si es el turno del jugador humano.
     *
     * @return {@code true} si es el turno del humano, {@code false} si es de la máquina.
     */
    public boolean isTurnoHumano() { return turnoHumano; }

    /**
     * Verifica si el juego está en la fase de batalla (después de la colocación).
     *
     * @return {@code true} si está en fase de batalla.
     */
    public boolean isFaseDeBatalla() { return faseDeBatalla; }

    /**
     * Verifica si el juego ha terminado.
     *
     * @return {@code true} si el juego terminó.
     */
    public boolean isJuegoTerminado() { return juegoTerminado; }

    /**
     * Obtiene el nickname del jugador humano.
     *
     * @return El {@code String} del nickname.
     */
    public String getNickname() { return nickname; }

    /**
     * Verifica si el modo revelar (mostrar barcos enemigos) está activado.
     *
     * @return {@code true} si el modo revelar está activo.
     */
    public boolean isModoRevelar() { return modoRevelar; } // <--- RECUPERARLO
}