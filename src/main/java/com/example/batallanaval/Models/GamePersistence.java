package com.example.batallanaval.Models;

import java.io.*;

/**
 * Clase encargada de manejar la persistencia de datos del juego.
 * Esto incluye la serialización y deserialización del estado del juego
 * (Guardar/Cargar juego), y la escritura de reportes de marcadores en archivos de texto.
 */
public class GamePersistence {

    /**
     * Guarda el estado actual del juego serializando el objeto {@code GameDTO}
     * en un archivo.
     *
     * @param gameDTO El objeto DTO que contiene el estado completo del juego.
     * @param nombreArchivo El nombre del archivo donde se guardará la serialización (ej. "partida.ser").
     */
    public void guardarJuegoSerializado(GameDTO gameDTO, String nombreArchivo) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            out.writeObject(gameDTO);
        } catch (IOException e) {
            System.err.println("Error guardando serialización: " + e.getMessage());
        }
    }

    /**
     * Guarda un reporte de marcador del juego en un archivo de texto plano.
     *
     * @param nickname El nickname del jugador.
     * @param hundidos El número de barcos enemigos hundidos por el jugador.
     * @param juegoActivo {@code true} si la partida todavía está en curso, {@code false} si ha terminado.
     * @param nombreArchivo El nombre del archivo de texto donde se escribirá el reporte (ej. "marcador.txt").
     */
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

    /**
     * Carga y deserializa el estado de un juego previamente guardado.
     *
     * @param nombreArchivo El nombre del archivo serializado a cargar.
     * @return El objeto {@code GameDTO} cargado, o {@code null} si el archivo no existe, es corrupto o incompatible.
     */
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

    /**
     * Verifica la existencia de un archivo de partida serializada para un nickname dado.
     *
     * @param nickname El nickname utilizado para generar el nombre de archivo esperado.
     * @return {@code true} si existe un archivo de partida guardada para ese nickname, {@code false} en caso contrario.
     */
    public boolean existePartida(String nickname) {
        return new File("batalla_naval_" + nickname + ".ser").exists();
    }
}