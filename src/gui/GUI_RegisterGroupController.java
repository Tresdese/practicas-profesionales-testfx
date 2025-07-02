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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GUI_RegisterGroupController {

    private static final int MAX_NRC_DIGITS = 5;

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
        configureTextFormatters();
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

    private void configureFieldToNumbers(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String onlyNumbers = newValue.replaceAll("[^\\d]", "");
            if (onlyNumbers.length() > MAX_NRC_DIGITS) {
                onlyNumbers = onlyNumbers.substring(0, MAX_NRC_DIGITS);
            }
            if (!newValue.equals(onlyNumbers)) {
                textField.setText(onlyNumbers);
            }
        });
    }

    private void configureTextFormatters() {
        nrcField.setTextFormatter(createTextFormatter(MAX_NRC_DIGITS));
        configureFieldToNumbers(nrcField);
    }

    private TextFormatter<String> createTextFormatter(int maxLength) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        );
    }

    private void loadAcademics() {
        try {
            UserDAO userDAO = new UserDAO();
            List<UserDTO> academics = userDAO.getAllUsers().stream()
                    .filter(user -> user.getRole() == Role.ACADEMIC)
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
                    for (UserDTO user : academicChoiceBox.getItems()) {
                        String fullName = user.getNames() + " " + user.getSurnames();
                        if (fullName.equals(string)) {
                            return user;
                        }
                    }
                    return new UserDTO();
                }
            });
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)){
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("40S02".equals(sqlState)) {
                statusLabel.setText("Tabla de grupos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de grupos no encontrada: {}", e);
            } else if ("40S22".equals(sqlState)) {
                statusLabel.setText("Columna de grupos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna de grupos no encontrada: {}", e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al inicializar el servicio.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al inicializar el servicio: {}", e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al cargar el archivo de configuracion.");
            LOGGER.error("Error al cargar el archivo de configuracion: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error al cargar grupos.");
            LOGGER.error("Error al cargar grupos: {}", e.getMessage(), e);
        }
    }

    private void loadPeriods() {
        try {
            PeriodDAO periodDAO = new PeriodDAO();
            List<PeriodDTO> periods = periodDAO.getAllPeriods();
            LOGGER.info("Periodos obtenidos: " + periods.size());
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
            if ("08001".equals(sqlState)){
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("40S02".equals(sqlState)) {
                statusLabel.setText("Tabla de grupos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de grupos no encontrada: {}", e);
            } else if ("40S22".equals(sqlState)) {
                statusLabel.setText("Columna de grupos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna de grupos no encontrada: {}", e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al inicializar el servicio.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al inicializar el servicio: {}", e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al cargar el archivo de configuracion.");
            LOGGER.error("Error al cargar el archivo de configuracion: {}", e.getMessage(), e);
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
            if ("08001".equals(sqlState)){
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos no encontrada: {}", e.getMessage(), e);
            } else if ("40S02".equals(sqlState)) {
                statusLabel.setText("Tabla de grupos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla de grupos no encontrada: {}", e);
            } else if ("40S22".equals(sqlState)) {
                statusLabel.setText("Columna de grupos no encontrada.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna de grupos no encontrada: {}", e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else if ("23000".equals(sqlState)) {
                statusLabel.setText("Violación de restricción de integridad.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Violación de restricción de integridad: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error al inicializar el servicio.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error al inicializar el servicio: {}", e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al cargar el archivo de configuracion.");
            LOGGER.error("Error al cargar el archivo de configuracion: {}", e.getMessage(), e);
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
        return !nrcField.getText().trim().isEmpty() &&
                !nameField.getText().trim().isEmpty() &&
                academicChoiceBox.getValue() != null &&
                periodChoiceBox.getValue() != null;
    }
}