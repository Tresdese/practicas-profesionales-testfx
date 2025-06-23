package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import logic.DTO.GroupDTO;
import logic.DAO.GroupDAO;
import logic.DAO.UserDAO;
import logic.DAO.PeriodDAO;
import logic.DTO.UserDTO;
import logic.DTO.Role;
import logic.DTO.PeriodDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GUI_RegisterGroupController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_RegisterGroupController.class);

    @FXML
    private Label statusLabel, nameCharCountLabel;

    @FXML
    private TextField nrcField, nameField;

    @FXML
    private ChoiceBox<UserDTO> academicChoiceBox;

    @FXML
    private ChoiceBox<PeriodDTO> periodChoiceBox;

    @FXML
    private Button registerButton;

    private ObservableList<UserDTO> academicList = FXCollections.observableArrayList();
    private ObservableList<PeriodDTO> periodList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadAcademics();
        loadPeriods();
        registerButton.setOnAction(event -> handleRegisterGroup());
        nameField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 50 ? change : null));
        nameCharCountLabel.setText("0/50");
        nameField.textProperty().addListener((observable, oldText, newText) ->
                nameCharCountLabel.setText(newText.length() + "/50")
        );
    }

    private void loadAcademics() {
        try {
            UserDAO userDAO = new UserDAO();
            List<UserDTO> academics = userDAO.getAllUsers().stream()
                    .filter(user -> user.getRole() == Role.ACADEMICO)
                    .collect(Collectors.toList());
            academicList.setAll(academics);
            academicChoiceBox.setItems(academicList);
            academicChoiceBox.setConverter(new javafx.util.StringConverter<UserDTO>() {
                @Override
                public String toString(UserDTO user) {
                    return user == null ? "" : user.getNames() + " " + user.getSurnames();
                }
                @Override
                public UserDTO fromString(String string) {
                    return null; //TODO
                }
            });
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumída con la base de datos.");
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar académicos.");
                LOGGER.error("Error al cargar académicos: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            statusLabel.setText("Error al cargar académicos.");
            LOGGER.error("Error al cargar académicos: {}", e.getMessage(), e);
        }
    }

    private void loadPeriods() {
        try {
            PeriodDAO periodDAO = new PeriodDAO();
            List<PeriodDTO> periods = periodDAO.getAllPeriods();
            periodList.setAll(periods);
            periodChoiceBox.setItems(periodList);
            periodChoiceBox.setConverter(new javafx.util.StringConverter<PeriodDTO>() {
                @Override
                public String toString(PeriodDTO period) {
                    return period == null ? "" : period.getName();
                }
                @Override
                public PeriodDTO fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al cargar periodos.");
                LOGGER.error("Error al cargar periodos: {}", e);
            }
        } catch (Exception e) {
            LOGGER.error("Error al cargar periodos: {}", e.getMessage(), e);
            statusLabel.setText("Error al cargar periodos.");
        }
    }

    @FXML
    private void handleRegisterGroup() {
        try {
            if (!areFieldsFilled()) {
                statusLabel.setText("Todos los campos deben estar llenos.");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            UserDTO selectedAcademic = academicChoiceBox.getValue();
            PeriodDTO selectedPeriod = periodChoiceBox.getValue();

            GroupDTO group = new GroupDTO(
                    nrcField.getText(),
                    nameField.getText(),
                    selectedAcademic.getIdUser(),
                    selectedPeriod.getIdPeriod()
            );

            GroupDAO groupDAO = new GroupDAO();
            boolean success = groupDAO.insertGroup(group);

            if (success) {
                statusLabel.setText("¡Grupo registrado exitosamente!");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                statusLabel.setText("No se pudo registrar el grupo.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al registrar el grupo.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al registrar el grupo: {}", e.getMessage(), e);
            }
        } catch (NullPointerException e) {
            LOGGER.error("Referencia nula al registrar el grupo: {}", e.getMessage(), e);
            statusLabel.setText("Error interno al registrar el grupo.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error al registrar el grupo: {}", e.getMessage(), e);
            statusLabel.setText("Ocurrió un error inesperado. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private boolean areFieldsFilled() {
        return !nrcField.getText().isEmpty() &&
                !nameField.getText().isEmpty() &&
                academicChoiceBox.getValue() != null &&
                periodChoiceBox.getValue() != null;
    }
}