package com.example.batallanaval.Models;

import java.io.Serializable;

public class GameDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Tablero tableroJugador;
    private MachineAI machineAI;
    private boolean turnoHumano;
    private boolean faseDeBatalla;
    private boolean juegoTerminado;
    private String nickname;
    private boolean modoRevelar; // <--- NUEVO CAMPO

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
    public Tablero getTableroJugador() { return tableroJugador; }
    public MachineAI getMachineAI() { return machineAI; }
    public boolean isTurnoHumano() { return turnoHumano; }
    public boolean isFaseDeBatalla() { return faseDeBatalla; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public String getNickname() { return nickname; }
    public boolean isModoRevelar() { return modoRevelar; } // <--- RECUPERARLO
}