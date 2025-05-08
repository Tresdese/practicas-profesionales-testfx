package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.DAO.ProjectPresentationDAO;
import logic.DTO.ProjectPresentationDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GUI_CheckListOfPresentationsController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListOfPresentationsController.class);

    @FXML
    private TableView<ProjectPresentationDTO> tableView;

    @FXML
    private TableColumn<ProjectPresentationDTO, Integer> columnIdPresentation;

    @FXML
    private TableColumn<ProjectPresentationDTO, String> columnIdProject;

    @FXML
    private TableColumn<ProjectPresentationDTO, String> columnDate;

    @FXML
    private TableColumn<ProjectPresentationDTO, String> columnType;

    private final ProjectPresentationDAO projectPresentationDAO = new ProjectPresentationDAO();

    @FXML
    public void initialize() {
        // Configurar las columnas
        columnIdPresentation.setCellValueFactory(new PropertyValueFactory<>("idPresentation"));
        columnIdProject.setCellValueFactory(new PropertyValueFactory<>("idProject"));
        columnDate.setCellValueFactory(cellData -> {
            // Formatear la fecha para mostrarla correctamente
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new javafx.beans.property.SimpleStringProperty(
                    dateFormat.format(cellData.getValue().getDate())
            );
        });
        columnType.setCellValueFactory(new PropertyValueFactory<>("tipe"));

        // Cargar las próximas presentaciones
        loadUpcomingPresentations();
    }

    private void loadUpcomingPresentations() {
        try {
            List<ProjectPresentationDTO> upcomingPresentations = projectPresentationDAO.getUpcomingPresentations();
            if (upcomingPresentations.isEmpty()) {
                logger.info("No hay presentaciones próximas para mostrar.");
            } else {
                ObservableList<ProjectPresentationDTO> data = FXCollections.observableArrayList(upcomingPresentations);
                tableView.setItems(data);
                logger.info("Próximas presentaciones cargadas exitosamente.");
            }
        } catch (SQLException e) {
            logger.error("Error al cargar las próximas presentaciones.", e);
        }
    }
}