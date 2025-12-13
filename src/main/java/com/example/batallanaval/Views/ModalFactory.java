package com.example.batallanaval.Views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.function.Consumer;

/**
 * Clase factoría encargada de crear y mostrar diferentes tipos de ventanas modales
 * (alertas, diálogos de confirmación, formularios de entrada, fin de juego)
 * dentro de un {@code StackPane} raíz de JavaFX.
 */
public class ModalFactory {

    private final StackPane rootPane;

    /**
     * Constructor para la factoría de modales.
     *
     * @param rootPane El {@code StackPane} principal de la escena donde se añadirán los modales.
     * Este pane debe ser el más alto en la jerarquía de la escena.
     */
    public ModalFactory(StackPane rootPane) {
        this.rootPane = rootPane;
    }

    /**
     * Muestra una ventana modal genérica de alerta con un solo botón de acción.
     *
     * @param titulo El título de la alerta.
     * @param mensaje El cuerpo del mensaje de la alerta.
     */
    public void mostrarAlerta(String titulo, String mensaje) {
        mostrarModalGenerico(titulo, mensaje, "Aceptar", () -> {});
    }

    /**
     * Muestra una ventana modal específica para el fin del juego (victoria o derrota).
     * El color del borde y del título se ajusta automáticamente según el título.
     *
     * @param titulo El título del modal (debe contener "VICTORIA" o similar para el color verde).
     * @param mensaje El mensaje de fin de juego.
     * @param onReiniciar La acción a ejecutar cuando se presiona el botón "JUGAR DE NUEVO".
     * @param onSalir La acción a ejecutar cuando se presiona el botón "SALIR".
     */
    public void mostrarModalFinJuego(String titulo, String mensaje, Runnable onReiniciar, Runnable onSalir) {
        StackPane modalRoot = crearFondoModal();
        VBox ventana = crearVentanaBase(500, 350);

        boolean esVictoria = titulo.toUpperCase().contains("VICTORIA");
        String color = esVictoria ? "#00FF00" : "#FF0000";
        ventana.setStyle(ventana.getStyle() + "-fx-border-color: " + color + ";");

        Label lblTitulo = crearLabel(titulo, 48, color);
        lblTitulo.setEffect(new DropShadow(20, Color.web(color)));
        Label lblMensaje = crearLabel(mensaje, 18, "WHITE");

        Button btnReiniciar = crearBoton("JUGAR DE NUEVO", color);
        btnReiniciar.setOnAction(e -> { cerrarModal(modalRoot); onReiniciar.run(); });

        Button btnSalir = crearBoton("SALIR", "gray");
        btnSalir.setOnAction(e -> onSalir.run());

        ventana.getChildren().addAll(lblTitulo, lblMensaje, btnReiniciar, btnSalir);
        mostrarEnRoot(modalRoot, ventana);
    }

    /**
     * Muestra un modal con un campo de texto para que el usuario ingrese un nickname.
     *
     * @param onAceptar La acción ({@code Consumer<String>}) a ejecutar cuando el usuario acepta,
     * recibiendo el nickname ingresado como argumento.
     */
    public void mostrarInputNickname(Consumer<String> onAceptar) {
        StackPane modalRoot = crearFondoModal();
        VBox ventana = crearVentanaBase(400, 300);

        TextField txt = new TextField();
        txt.setStyle("-fx-background-color: #002b4d; -fx-text-fill: #00cdc9; -fx-border-color: #00cdc9;");
        txt.setMaxWidth(200);

        Button btn = crearBoton("INGRESAR", "#00cdc9");
        btn.setOnAction(e -> {
            if (!txt.getText().trim().isEmpty()) { cerrarModal(modalRoot); onAceptar.accept(txt.getText().trim()); }
        });

        ventana.getChildren().addAll(crearLabel("IDENTIFICACIÓN", 22, "#00cdc9"),
                crearLabel("Ingresa tu Nickname:", 16, "WHITE"), txt, btn);
        mostrarEnRoot(modalRoot, ventana);
    }

    /**
     * Muestra un modal de confirmación con dos opciones ("Sí" y "No").
     *
     * @param titulo El título de la confirmación.
     * @param mensaje El mensaje que requiere confirmación.
     * @param txtSi El texto del botón de confirmación positiva.
     * @param txtNo El texto del botón de confirmación negativa.
     * @param onSi La acción a ejecutar al presionar el botón de confirmación positiva.
     * @param onNo La acción a ejecutar al presionar el botón de confirmación negativa.
     */
    public void mostrarConfirmacion(String titulo, String mensaje, String txtSi, String txtNo, Runnable onSi, Runnable onNo) {
        StackPane modalRoot = crearFondoModal();
        VBox ventana = crearVentanaBase(400, 300);

        Button btnSi = crearBoton(txtSi, "#00cdc9");
        btnSi.setOnAction(e -> { cerrarModal(modalRoot); onSi.run(); });

        Button btnNo = crearBoton(txtNo, "#ff4444");
        btnNo.setOnAction(e -> { cerrarModal(modalRoot); onNo.run(); });

        VBox botones = new VBox(15, btnSi, btnNo);
        botones.setAlignment(Pos.CENTER);

        ventana.getChildren().addAll(crearLabel(titulo, 22, "#00cdc9"),
                crearLabel(mensaje, 14, "WHITE"), botones);
        mostrarEnRoot(modalRoot, ventana);
    }

    // --- Helpers Privados ---
    /**
     * Método privado que encapsula la lógica para mostrar un modal genérico.
     *
     * @param titulo El título del modal.
     * @param msg El mensaje del modal.
     * @param btnTxt El texto del botón de cierre.
     * @param onClose La acción a ejecutar al cerrar el modal.
     */
    private void mostrarModalGenerico(String titulo, String msg, String btnTxt, Runnable onClose) {
        StackPane root = crearFondoModal();
        VBox box = crearVentanaBase(400, 300);
        Button btn = crearBoton(btnTxt, "#00cdc9");
        btn.setOnAction(e -> { cerrarModal(root); onClose.run(); });
        box.getChildren().addAll(crearLabel(titulo, 22, "#00cdc9"), crearLabel(msg, 14, "WHITE"), btn);
        mostrarEnRoot(root, box);
    }

    /**
     * Crea el panel de fondo oscuro y semitransparente para simular el efecto de 'glass pane'.
     *
     * @return Un {@code StackPane} que cubre toda la escena.
     */
    private StackPane crearFondoModal() {
        StackPane glass = new StackPane();
        glass.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        return glass;
    }

    /**
     * Crea la caja contenedora vertical (VBox) que sirve como la ventana central del modal.
     *
     * @param w El ancho máximo de la ventana.
     * @param h El alto máximo de la ventana.
     * @return Un {@code VBox} con estilo de ventana base.
     */
    private VBox crearVentanaBase(int w, int h) {
        VBox v = new VBox(20);
        v.setAlignment(Pos.CENTER);
        v.setMaxSize(w, h);
        v.setStyle("-fx-background-color: #001933; -fx-border-width: 2; -fx-padding: 30; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #00cdc9;");
        return v;
    }

    /**
     * Crea un {@code Label} estilizado con fuente Arial negrita, tamaño y color especificados.
     *
     * @param txt El texto del Label.
     * @param size El tamaño de la fuente.
     * @param color El color del texto (puede ser un nombre o código HEX).
     * @return El {@code Label} estilizado.
     */
    private Label crearLabel(String txt, int size, String color) {
        Label l = new Label(txt);
        l.setTextFill(Color.web(color));
        l.setFont(Font.font("Arial", FontWeight.BOLD, size));
        l.setWrapText(true);
        l.setTextAlignment(TextAlignment.CENTER);
        return l;
    }

    /**
     * Crea un {@code Button} estilizado con fondo transparente, borde y color de texto.
     *
     * @param txt El texto del botón.
     * @param color El color del texto y del borde (código HEX o nombre).
     * @return El {@code Button} estilizado.
     */
    private Button crearBoton(String txt, String color) {
        Button b = new Button(txt);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + "; -fx-border-color: " + color + "; -fx-font-weight: bold; -fx-cursor: hand; -fx-pref-width: 180;");
        return b;
    }

    /**
     * Remueve el modal del {@code StackPane} raíz, cerrando la ventana modal.
     *
     * @param modal El {@code StackPane} que contiene el modal a cerrar.
     */
    private void cerrarModal(StackPane modal) { rootPane.getChildren().remove(modal); }

    /**
     * Añade el contenido (la ventana base) al fondo del modal y luego añade el modal
     * completo al {@code StackPane} raíz, haciéndolo visible.
     *
     * @param modal El {@code StackPane} de fondo (glass pane).
     * @param content La caja {@code VBox} que contiene el contenido visible de la ventana.
     */
    private void mostrarEnRoot(StackPane modal, VBox content) { modal.getChildren().add(content); rootPane.getChildren().add(modal); }
}