package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.DAO.StudentProjectViewDAO;
import logic.DTO.StudentProjectViewDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListOfParticipantsController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListOfParticipantsController.class);

    @FXML
    private TableView<StudentProjectViewDTO> tableView;

    @FXML
    private TableColumn<StudentProjectViewDTO, String> columnStudentMatricula;

    @FXML
    private TableColumn<StudentProjectViewDTO, String> columnStudentName;

    @FXML
    private TableColumn<StudentProjectViewDTO, String> columnProjectName;

    private final StudentProjectViewDAO studentProjectViewDAO = new StudentProjectViewDAO();
    private int presentationId = -1; // Valor predeterminado para indicar que no está definido

    public void setPresentationId(int presentationId) {
        this.presentationId = presentationId;
        logger.info("ID de la presentación recibido: " + presentationId);
        loadStudentProjectData(); // Cargar los datos al recibir el ID de la presentación
    }

    @FXML
    public void initialize() {
        // Configurar las columnas
        columnStudentMatricula.setCellValueFactory(new PropertyValueFactory<>("studentMatricula"));
        columnStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        columnProjectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
    }

    private void loadStudentProjectData() {
        if (presentationId <= 0) { // Verificar si el ID de la presentación es válido
            logger.warn("El ID de la presentación no es válido: " + presentationId);
            return;
        }

        logger.info("Cargando datos para la presentación con ID: " + presentationId);

        try {
            List<StudentProjectViewDTO> studentProjectData = studentProjectViewDAO.getStudentProjectViewByPresentationId(presentationId);
            if (studentProjectData.isEmpty()) {
                logger.warn("No se encontraron datos para la presentación con ID: " + presentationId);
            } else {
                ObservableList<StudentProjectViewDTO> data = FXCollections.observableArrayList(studentProjectData);
                tableView.setItems(data);
                logger.info("Datos cargados exitosamente en la tabla.");
            }
        } catch (SQLException e) {
            logger.error("Error al cargar los datos de estudiantes y proyectos.", e);
        }
    }
}