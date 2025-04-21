package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GUI_RegistrarEstudianteController {

    @FXML
    private Label label;

    @FXML
    public void initialize() {
        // Código que se ejecuta al cargar la interfaz
        label.setText("¡Interfaz inicializada!");
    }


}
