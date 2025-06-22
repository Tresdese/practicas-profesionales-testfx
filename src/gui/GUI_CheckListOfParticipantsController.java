package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.DAO.StudentProjectViewDAO;
import logic.DTO.StudentProjectViewDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListOfParticipantsController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListOfParticipantsController.class);

    @FXML
    private TableView<StudentProjectViewDTO> participantsTableView;

    @FXML
    private TableColumn<StudentProjectViewDTO, String> studentTuitionColumn;

    @FXML
    private TableColumn<StudentProjectViewDTO, String> studentNameColumn;

    @FXML
    private TableColumn<StudentProjectViewDTO, String> projectNameColumn;

    @FXML
    private TableColumn<StudentProjectViewDTO, Void> gradePresentationColumn;

    @FXML
    private Label participantCountsLabel;

    private final StudentProjectViewDAO studentProjectViewDAO = new StudentProjectViewDAO();
    private int presentationId = -1;

    public void setPresentationId(int presentationId) {
        this.presentationId = presentationId;
        logger.info("ID de la presentación recibido: " + presentationId);
        loadStudentProjectData();
    }

    @FXML
    public void initialize() {
        studentTuitionColumn.setCellValueFactory(new PropertyValueFactory<>("studentMatricula"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));

        addGradePresentationButtonToTable();
    }

    private void loadStudentProjectData() {
        if (presentationId <= 0) {
            logger.warn("El ID de la presentación no es válido: " + presentationId);
            participantsTableView.setItems(FXCollections.observableArrayList());
            updateParticipantCounts(FXCollections.observableArrayList());
            return;
        }

        logger.info("Cargando datos para la presentación con ID: " + presentationId);

        try {
            List<StudentProjectViewDTO> studentProjectData = studentProjectViewDAO.getStudentProjectViewByPresentationId(presentationId);
            ObservableList<StudentProjectViewDTO> data = FXCollections.observableArrayList(studentProjectData);
            participantsTableView.setItems(data);
            updateParticipantCounts(data);
            if (studentProjectData.isEmpty()) {
                logger.warn("No se encontraron datos para la presentación con ID: " + presentationId);
            } else {
                logger.info("Datos cargados exitosamente en la tabla.");
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                logger.error("Error de conexión con la base de datos: " + e.getMessage(), e);
                showAlert("Error de conexión con la base de datos.");
                participantCountsLabel.setText("Error de conexión");
            } else if ("08S01".equals(sqlState)) {
                logger.error("Error de interrupcion de conexión con la base de datos: " + e.getMessage(), e);
                showAlert("Error de interrupcion de conexión con la base de datos.");
                participantCountsLabel.setText("Error de conexión");
            } else if ("42000".equals(sqlState)) {
                logger.error("Base de datos desconocida: " + e.getMessage(), e);
                showAlert("Base de datos desconocida. Por favor, verifica la configuración.");
                participantCountsLabel.setText("Base de datos desconocida");
            } else if ("28000".equals(sqlState)) {
                logger.error("Acceso denegado a la base de datos: " + e.getMessage(), e);
                showAlert("Acceso denegado a la base de datos. Por favor, verifica tus credenciales.");
                participantCountsLabel.setText("Acceso denegado");
            } else {
                logger.error("Error de base de datos al cargar los datos de la presentación: " + e.getMessage(), e);
                showAlert("Error de base de datos al cargar los datos de la presentación. Por favor, intenta nuevamente.");
                participantCountsLabel.setText("Error al cargar los datos");
            }
        } catch (IOException e) {
            logger.error("Error al leer la configuracion de la base de datos: " + e.getMessage(), e);
            showAlert("Error al leer la configuracion de la base de datos.");
            participantCountsLabel.setText("Error al leer la configuracion de la base de datos.");
        } catch (Exception e) {
            logger.error("Error inesperado al cargar los datos de la presentación: " + e.getMessage(), e);
            showAlert("Error inesperado al cargar los datos de la presentación.");
            participantCountsLabel.setText("Error inesperado");
        }
    }

    private void updateParticipantCounts(ObservableList<StudentProjectViewDTO> list) {
        int total = list.size();
        participantCountsLabel.setText("Totales: " + total);
    }

    private void addGradePresentationButtonToTable() {
        gradePresentationColumn.setCellFactory(param -> new TableCell<>() {
            private final Button gradeButton = new Button("Calificar Presentación");

            {
                gradeButton.setOnAction(event -> {
                    StudentProjectViewDTO selectedParticipant = getTableView().getItems().get(getIndex());
                    openGradePresentationWindow(selectedParticipant);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(gradeButton);
                }
            }
        });
    }

    private void openGradePresentationWindow(StudentProjectViewDTO participant) {
        try {
            logger.info("Abriendo ventana para calificar la presentación del estudiante: " + participant.getStudentName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_EvaluatePresentation.fxml"));
            Parent root = loader.load();

            GUI_EvaluatePresentationController controller = loader.getController();
            controller.setPresentationIdAndTuiton(participant.getIdPresentation(), participant.getStudentMatricula());
            controller.loadCriteria();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Calificar Presentación - " + participant.getStudentName());
            stage.show();
        } catch (IOException e) {
            logger.error("Error al abrir la ventana de calificación.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al abrir la ventana de calificación.", e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}