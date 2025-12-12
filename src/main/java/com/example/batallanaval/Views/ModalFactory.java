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

public class ModalFactory {

    private final StackPane rootPane;

    public ModalFactory(StackPane rootPane) {
        this.rootPane = rootPane;
    }

    public void mostrarAlerta(String titulo, String mensaje) {
        mostrarModalGenerico(titulo, mensaje, "Aceptar", () -> {});
    }

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
    private void mostrarModalGenerico(String titulo, String msg, String btnTxt, Runnable onClose) {
        StackPane root = crearFondoModal();
        VBox box = crearVentanaBase(400, 300);
        Button btn = crearBoton(btnTxt, "#00cdc9");
        btn.setOnAction(e -> { cerrarModal(root); onClose.run(); });
        box.getChildren().addAll(crearLabel(titulo, 22, "#00cdc9"), crearLabel(msg, 14, "WHITE"), btn);
        mostrarEnRoot(root, box);
    }

    private StackPane crearFondoModal() {
        StackPane glass = new StackPane();
        glass.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        return glass;
    }

    private VBox crearVentanaBase(int w, int h) {
        VBox v = new VBox(20);
        v.setAlignment(Pos.CENTER);
        v.setMaxSize(w, h);
        v.setStyle("-fx-background-color: #001933; -fx-border-width: 2; -fx-padding: 30; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #00cdc9;");
        return v;
    }

    private Label crearLabel(String txt, int size, String color) {
        Label l = new Label(txt);
        l.setTextFill(Color.web(color));
        l.setFont(Font.font("Arial", FontWeight.BOLD, size));
        l.setWrapText(true);
        l.setTextAlignment(TextAlignment.CENTER);
        return l;
    }

    private Button crearBoton(String txt, String color) {
        Button b = new Button(txt);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: " + color + "; -fx-border-color: " + color + "; -fx-font-weight: bold; -fx-cursor: hand; -fx-pref-width: 180;");
        return b;
    }

    private void cerrarModal(StackPane modal) { rootPane.getChildren().remove(modal); }
    private void mostrarEnRoot(StackPane modal, VBox content) { modal.getChildren().add(content); rootPane.getChildren().add(modal); }
}