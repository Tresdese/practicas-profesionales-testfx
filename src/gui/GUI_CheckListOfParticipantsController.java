package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
            logger.error("Error al cargar los datos de estudiantes y proyectos.", e);
            participantsTableView.setItems(FXCollections.observableArrayList());
            updateParticipantCounts(FXCollections.observableArrayList());
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
}