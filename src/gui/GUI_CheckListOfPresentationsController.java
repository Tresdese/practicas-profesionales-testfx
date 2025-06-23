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
import javafx.scene.paint.Color;
import javafx.util.Callback;
import logic.DAO.ProjectPresentationDAO;
import logic.DTO.ProjectPresentationDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GUI_CheckListOfPresentationsController {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckListOfPresentationsController.class);

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
            LOGGER.info("Cantidad de presentaciones obtenidas: " + upcomingPresentations.size());

            ObservableList<ProjectPresentationDTO> data = FXCollections.observableArrayList(upcomingPresentations);
            presentationsTableView.setItems(data);
            updatePresentationCounts(data);

            if (upcomingPresentations.isEmpty()) {
                LOGGER.info("No hay presentaciones próximas para mostrar.");
                statusLabel.setText("No hay presentaciones próximas para mostrar.");
            } else {
                for (ProjectPresentationDTO presentation : upcomingPresentations) {
                    LOGGER.info("Presentación obtenida: ID=" + presentation.getIdPresentation() +
                            ", Proyecto=" + presentation.getIdProject() +
                            ", Fecha=" + presentation.getDate() +
                            ", Tipo=" + presentation.getTipe());
                }
                LOGGER.info("Próximas presentaciones cargadas exitosamente.");
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, intenta de nuevo.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna desconocida en la base de datos. Por favor, verifica la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla desconocida en la base de datos. Por favor, verifica la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos. Por favor, intenta de nuevo.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifica la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos. Por favor, verifica tus credenciales.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al cargar las presentaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al cargar las presentaciones: {}", e.getMessage(), e);
            }
        }  catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar las presentaciones.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al cargar las presentaciones: {}", e.getMessage(), e);
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
            String sqlState = e.getSQLState();
            if (sqlState != null && sqlState.equals("08001")) {
                statusLabel.setText("Error de conexión con la base de datos. Por favor, verifica tu conexión.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42000")) {
                statusLabel.setText("Base de datos desconocida. Por favor, verifica la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S22")) {
                statusLabel.setText("Columna desconocida en la base de datos. Por favor, verifica la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("42S02")) {
                statusLabel.setText("Tabla desconocida en la base de datos. Por favor, verifica la configuración.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("08S01")) {
                statusLabel.setText("Conexión interrumpida con la base de datos. Por favor, intenta de nuevo.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("HY000")) {
                statusLabel.setText("Error general de la base de datos. Por favor, intenta de nuevo.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if (sqlState != null && sqlState.equals("28000")) {
                statusLabel.setText("Acceso denegado a la base de datos. Por favor, verifica tus credenciales.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al buscar presentaciones.");
                statusLabel.setTextFill(Color.RED);
                LOGGER.error("Error de base de datos al buscar presentaciones: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al buscar presentaciones.");
            statusLabel.setTextFill(Color.RED);
            LOGGER.error("Error inesperado al buscar presentaciones: {}", e.getMessage(), e);
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
            LOGGER.info("Abriendo ventana de Lista de Participantes para la presentación con ID: " + presentationId);
            controller.setPresentationId(presentationId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Lista de Participantes");
            stage.show();

            LOGGER.info("Ventana de Lista de Participantes abierta correctamente.");
        } catch (IOException e) {
            LOGGER.error("Error al abrir el fxml de la ventana de Lista de Participantes.", e);
            statusLabel.setText("Error al abrir el fxml de la ventana de Lista de Participantes.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de Lista de Participantes.", e);
            statusLabel.setText("Error inesperado al abrir la ventana de Lista de Participantes.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void openRegisterPresentationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterPresentation.fxml"));
            Parent root = loader.load();

            GUI_RegisterPresentationController controller = loader.getController();
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Presentación");
            stage.show();

            LOGGER.info("Ventana de Registrar Presentación abierta correctamente.");
        } catch (IOException e) {
            LOGGER.error("Error al abrir el fxml de la ventana de Registrar Presentación.", e);
            statusLabel.setText("Error al abrir el fxml de la ventana de Registrar Presentación.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al abrir la ventana de Registrar Presentación.", e);
            statusLabel.setText("Error inesperado al abrir la ventana de Registrar Presentación.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public void reloadPresentations() {
        loadUpcomingPresentations();
    }
}