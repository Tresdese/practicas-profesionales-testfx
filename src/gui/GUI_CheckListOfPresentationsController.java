package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    private TableView<ProjectPresentationDTO> presentationsTableView;

    @FXML
    private TableColumn<ProjectPresentationDTO, Integer> idPresentationColumn;

    @FXML
    private TableColumn<ProjectPresentationDTO, String> idProjectColumn;

    @FXML
    private TableColumn<ProjectPresentationDTO, String> dateColumn;

    @FXML
    private TableColumn<ProjectPresentationDTO, String> typeColumn;

    @FXML
    private TableColumn<ProjectPresentationDTO, Void> registerEvaluationColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label presentationCountsLabel;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button registerPresentationButton;

    private final ProjectPresentationDAO projectPresentationDAO = new ProjectPresentationDAO();
    private ProjectPresentationDTO selectedPresentation;

    @FXML
    public void initialize() {
        idPresentationColumn.setCellValueFactory(new PropertyValueFactory<>("idPresentation"));
        idProjectColumn.setCellValueFactory(new PropertyValueFactory<>("idProject"));
        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new javafx.beans.property.SimpleStringProperty(
                    dateFormat.format(cellData.getValue().getDate())
            );
        });
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("tipe"));

        addRegisterEvaluationButtonToTable();

        loadUpcomingPresentations();

        presentationsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedPresentation = newValue;
            presentationsTableView.refresh();
        });

        searchButton.setOnAction(event -> searchPresentation());
        registerPresentationButton.setOnAction(event -> openRegisterPresentationWindow());
    }

    private void loadUpcomingPresentations() {
        try {
            List<ProjectPresentationDTO> upcomingPresentations = projectPresentationDAO.getUpcomingPresentations();
            logger.info("Cantidad de presentaciones obtenidas: " + upcomingPresentations.size());

            ObservableList<ProjectPresentationDTO> data = FXCollections.observableArrayList(upcomingPresentations);
            presentationsTableView.setItems(data);
            updatePresentationCounts(data);

            if (upcomingPresentations.isEmpty()) {
                logger.info("No hay presentaciones próximas para mostrar.");
            } else {
                for (ProjectPresentationDTO presentation : upcomingPresentations) {
                    logger.info("Presentación obtenida: ID=" + presentation.getIdPresentation() +
                            ", Proyecto=" + presentation.getIdProject() +
                            ", Fecha=" + presentation.getDate() +
                            ", Tipo=" + presentation.getTipe());
                }
                logger.info("Próximas presentaciones cargadas exitosamente.");
            }
        } catch (SQLException e) {
            logger.error("Error al cargar las próximas presentaciones.", e);
            presentationsTableView.setItems(FXCollections.observableArrayList());
            updatePresentationCounts(FXCollections.observableArrayList());
        }
    }

    private void searchPresentation() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadUpcomingPresentations();
            return;
        }

        ObservableList<ProjectPresentationDTO> filteredList = FXCollections.observableArrayList();

        try {
            try {
                int id = Integer.parseInt(searchQuery);
                ProjectPresentationDTO presentation = projectPresentationDAO.searchProjectPresentationById(id);
                if (presentation != null) {
                    filteredList.add(presentation);
                }
            } catch (NumberFormatException e) {
                List<ProjectPresentationDTO> presentations = projectPresentationDAO.searchProjectPresentationsByProjectId(searchQuery);
                filteredList.addAll(presentations);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar presentaciones.");
            logger.error("Error al buscar presentaciones: {}", e.getMessage(), e);
        }

        presentationsTableView.setItems(filteredList);
        updatePresentationCounts(filteredList);
    }

    private void updatePresentationCounts(ObservableList<ProjectPresentationDTO> list) {
        int total = list.size();
        presentationCountsLabel.setText("Totales: " + total);
    }

    private void addRegisterEvaluationButtonToTable() {
        Callback<TableColumn<ProjectPresentationDTO, Void>, TableCell<ProjectPresentationDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button registerButton = new Button("Registrar Evaluación");

            {
                registerButton.setOnAction(event -> {
                    ProjectPresentationDTO presentation = getTableView().getItems().get(getIndex());
                    openCheckListOfParticipantsWindow(presentation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) != selectedPresentation) {
                    setGraphic(null);
                } else {
                    setGraphic(registerButton);
                }
            }
        };

        registerEvaluationColumn.setCellFactory(cellFactory);
    }

    private void openCheckListOfParticipantsWindow(ProjectPresentationDTO presentation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfParticipants.fxml"));
            Parent root = loader.load();

            GUI_CheckListOfParticipantsController controller = loader.getController();
            int presentationId = presentation.getIdPresentation();
            logger.info("Abriendo ventana de Lista de Participantes para la presentación con ID: " + presentationId);
            controller.setPresentationId(presentationId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Lista de Participantes");
            stage.show();

            logger.info("Ventana de Lista de Participantes abierta correctamente.");
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de Lista de Participantes.", e);
        }
    }

    private void openRegisterPresentationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterPresentation.fxml"));
            Parent root = loader.load();

            GUI_RegisterPresentationController controller = loader.getController();
            controller.setParentController(this); // Pasa la referencia

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Presentación");
            stage.show();

            logger.info("Ventana de Registrar Presentación abierta correctamente.");
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de Registrar Presentación.", e);
        }
    }

    public void reloadPresentations() {
        loadUpcomingPresentations();
    }
}