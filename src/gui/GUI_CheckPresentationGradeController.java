package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.DAO.EvaluationPresentationDAO;
import logic.DTO.EvaluationPresentationDTO;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GUI_CheckPresentationGradeController {

    @FXML
    private TableView<EvaluationPresentationDTO> tableView;
    @FXML
    private TableColumn<EvaluationPresentationDTO, Integer> colIdEvaluacion;
    @FXML
    private TableColumn<EvaluationPresentationDTO, Integer> colIdPresentacion;
    @FXML
    private TableColumn<EvaluationPresentationDTO, String> colFecha;
    @FXML
    private TableColumn<EvaluationPresentationDTO, Double> colPromedio;
    @FXML
    private TableColumn<EvaluationPresentationDTO, Void> colVerDetalles;
    @FXML
    private Label statusLabel;

    private static final Logger logger = LogManager.getLogger(GUI_CheckPresentationGradeController.class);

    private StudentDTO student;

    public void setStudent(StudentDTO student) {
        this.student = student;
        loadEvaluations();
    }

    @FXML
    public void initialize() {
        colIdEvaluacion.setCellValueFactory(new PropertyValueFactory<>("idEvaluation"));
        colIdPresentacion.setCellValueFactory(new PropertyValueFactory<>("idProject"));
        colFecha.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return new SimpleStringProperty(cellData.getValue().getDate() != null ? sdf.format(cellData.getValue().getDate()) : "");
        });
        colPromedio.setCellValueFactory(new PropertyValueFactory<>("average"));

        addViewDetailsButtonToTable();
    }

    private void addViewDetailsButtonToTable() {
        Callback<TableColumn<EvaluationPresentationDTO, Void>, TableCell<EvaluationPresentationDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Ver Detalles");

            {
                btn.setOnAction(event -> {
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
                    setGraphic(btn);
                }
            }
        };
        colVerDetalles.setCellFactory(cellFactory);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> tableView.refresh());
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
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de detalles de la presentación: {}", e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo abrir la ventana de detalles.");
            alert.showAndWait();
        }
    }

    private void loadEvaluations() {
        try {
            tableView.getItems().clear();
            statusLabel.setText("");

            if (student == null) {
                statusLabel.setText("No se encontró información del estudiante.");
                return;
            }

            EvaluationPresentationDAO evaluationDAO = new EvaluationPresentationDAO();
            List<EvaluationPresentationDTO> studentEvaluations = evaluationDAO.getEvaluationPresentationsByTuiton(student.getTuiton());

            if (studentEvaluations == null || studentEvaluations.isEmpty()) {
                statusLabel.setText("No tienes evaluaciones de presentación registradas.");
            } else {
                tableView.getItems().setAll(studentEvaluations);
            }
        } catch (SQLException e) {
            logger.error("Error de base de datos al cargar las evaluaciones: {}", e.getMessage(), e);
            statusLabel.setText("Error de base de datos al cargar las evaluaciones.");
        } catch (Exception e) {
            logger.error("Error inesperado al cargar las evaluaciones: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al cargar las evaluaciones.");
        }
    }
}