package com.example.batallanaval.Models;

import java.io.*;

public class GamePersistence {

    public void guardarJuegoSerializado(GameDTO gameDTO, String nombreArchivo) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            out.writeObject(gameDTO);
        } catch (IOException e) {
            System.err.println("Error guardando serialización: " + e.getMessage());
        }
    }

    public void guardarMarcadorTxt(String nickname, int hundidos, boolean juegoActivo, String nombreArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write("--- BATALLA NAVAL REPORTE ---");
            writer.newLine();
            writer.write("Nickname: " + nickname);
            writer.newLine();
            writer.write("Barcos Enemigos Hundidos: " + hundidos);
            writer.newLine();
            writer.write("Estado Partida: " + (juegoActivo ? "En Curso" : "Terminada"));
        } catch (IOException e) {
            System.err.println("Error escribiendo archivo plano: " + e.getMessage());
        }
    }

    public GameDTO cargarJuego(String nombreArchivo) {
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (GameDTO) in.readObject();
        } catch (Exception e) {
            System.out.println("Archivo corrupto o incompatible.");
            return null;
        }
    }

    public boolean existePartida(String nickname) {
        return new File("batalla_naval_" + nickname + ".ser").exists();
    }
}