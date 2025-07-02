package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import logic.DAO.EvaluationPresentationDAO;
import logic.DTO.EvaluationPresentationDTO;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GUI_CheckPresentationGradeController {

    @FXML
    private TableView<EvaluationPresentationDTO> presentationGradeTableView;

    @FXML
    private TableColumn<EvaluationPresentationDTO, Integer> idEvaluationColumn;

    @FXML
    private TableColumn<EvaluationPresentationDTO, Integer> idPresentationColumn;

    @FXML
    private TableColumn<EvaluationPresentationDTO, String> dateColumn;

    @FXML
    private TableColumn<EvaluationPresentationDTO, Double> averageColumn;

    @FXML
    private TableColumn<EvaluationPresentationDTO, Void> seeDetailsColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label evaluationCountsLabel;

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckPresentationGradeController.class);

    private StudentDTO student;

    public void setStudent(StudentDTO student) {
        this.student = student;
        loadEvaluations();
    }

    @FXML
    public void initialize() {
        idEvaluationColumn.setCellValueFactory(new PropertyValueFactory<>("idEvaluation"));
        idPresentationColumn.setCellValueFactory(new PropertyValueFactory<>("idProject"));
        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return new SimpleStringProperty(cellData.getValue().getDate() != null ? simpleDateFormat.format(cellData.getValue().getDate()) : "");
        });
        averageColumn.setCellValueFactory(new PropertyValueFactory<>("average"));

        addViewDetailsButtonToTable();
    }

    private void addViewDetailsButtonToTable() {
        Callback<TableColumn<EvaluationPresentationDTO, Void>, TableCell<EvaluationPresentationDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button seeDetailsButton = new Button("Ver Detalles");

            {
                seeDetailsButton.setOnAction(event -> {
                    EvaluationPresentationDTO evaluation = getTableView().getItems().get(getIndex());
                    showDetailsDialog(evaluation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(seeDetailsButton);
                }
            }
        };
        seeDetailsColumn.setCellFactory(cellFactory);

        presentationGradeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> presentationGradeTableView.refresh());
    }

    private void showDetailsDialog(EvaluationPresentationDTO evaluation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_DetailsPresentationStudent.fxml"));
            Parent root = loader.load();

            GUI_DetailsPresentationStudentController controller = loader.getController();
            controller.setIdEvaluation(evaluation.getIdEvaluation());

            Stage stage = new Stage();
            stage.setTitle("Detalles de la Evaluación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.error("Error al cargar la ventana de detalles de la presentación: {}", e.getMessage(), e);
            showAlert("No se pudo cargar la ventana de detalles.");
        } catch (Exception e) {
            LOGGER.error("Error al abrir la ventana de detalles de la presentación: {}", e.getMessage(), e);
            showAlert("No se pudo abrir la ventana de detalles.");
        }
    }

    private void loadEvaluations() {
        try {
            presentationGradeTableView.getItems().clear();
            statusLabel.setText("");

            if (student == null) {
                statusLabel.setText("No se encontró información del estudiante.");
                updateEvaluationCounts(FXCollections.observableArrayList());
                return;
            }

            EvaluationPresentationDAO evaluationDAO = new EvaluationPresentationDAO();
            List<EvaluationPresentationDTO> studentEvaluations = evaluationDAO.getEvaluationPresentationsByTuition(student.getTuition());

            ObservableList<EvaluationPresentationDTO> data = FXCollections.observableArrayList(studentEvaluations);

            if (studentEvaluations == null || studentEvaluations.isEmpty()) {
                statusLabel.setText("No tienes evaluaciones de presentación registradas.");
            }
            presentationGradeTableView.setItems(data);
            updateEvaluationCounts(data);

        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                updateEvaluationCounts(FXCollections.observableArrayList());
            } else if (sqlState != null && sqlState.equals("08S01")) {
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                updateEvaluationCounts(FXCollections.observableArrayList());
            } else if (sqlState != null && sqlState.equals("42S22")) {
                LOGGER.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Columna desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                updateEvaluationCounts(FXCollections.observableArrayList());
            } else if (sqlState != null && sqlState.equals("42S02")) {
                LOGGER.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Tabla desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                updateEvaluationCounts(FXCollections.observableArrayList());
            } else if (sqlState != null && sqlState.equals("HY000")) {
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                updateEvaluationCounts(FXCollections.observableArrayList());
            } else if (sqlState != null && sqlState.equals("42000")) {
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                updateEvaluationCounts(FXCollections.observableArrayList());
            } else if (sqlState != null && sqlState.equals("28000")) {
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                updateEvaluationCounts(FXCollections.observableArrayList());
            } else {
                LOGGER.error("Error de base de datos al cargar las evaluaciones: {}", e.getMessage(), e);
                statusLabel.setText("Error de base de datos al cargar las evaluaciones.");
                updateEvaluationCounts(FXCollections.observableArrayList());
            }
        } catch (IOException e) {
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            updateEvaluationCounts(FXCollections.observableArrayList());
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar las evaluaciones: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al cargar las evaluaciones.");
            updateEvaluationCounts(FXCollections.observableArrayList());
        }
    }

    private void updateEvaluationCounts(ObservableList<EvaluationPresentationDTO> list) {
        int total = list.size();
        evaluationCountsLabel.setText("Totales: " + total);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}