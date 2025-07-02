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

import logic.DAO.ProjectDAO;
import logic.DAO.StudentProjectDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import logic.DTO.UserStudentViewDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import logic.DTO.Role;
import logic.DAO.UserStudentViewDAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_CheckListOfStudentsController {

    private static final Logger logger = LogManager.getLogger(GUI_CheckListOfStudentsController.class);

    @FXML
    private TableView<StudentDTO> tableView;

    @FXML
    private TableColumn<StudentDTO, String> tuitionColumn;

    @FXML
    private TableColumn<StudentDTO, String> namesColumn;

    @FXML
    private TableColumn<StudentDTO, String> surnamesColumn;

    @FXML
    private TableColumn<StudentDTO, String> emailColumn;

    @FXML
    private TableColumn<StudentDTO, String> nrcColumn;

    @FXML
    private TableColumn<StudentDTO, Void> detailsColumn;

    @FXML
    private TableColumn<StudentDTO, Void> managementColumn;

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> filterChoiceBox;

    @FXML
    private Button searchButton;

    @FXML
    private Button registerStudentButton;

    @FXML
    private Button assignProjectButton;

    @FXML
    private Button reassignProjectButton;

    @FXML
    private Button deleteStudentButton;

    @FXML
    private Label statusLabel, studentCountsLabel;

    private StudentDTO selectedStudent;
    private StudentService studentService;
    private StudentProjectDTO studentProject;
    private ProjectDTO currentProject = null;
    private Role userRole;

    private int idUserAcademic = -1;

    public void setIdUserAcademic(int idUsuario) {
        this.idUserAcademic = idUsuario;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
        applyRoleRestrictions();
    }

    public void initialize() {
        boolean hasInitializationError = false;

        try {
            this.studentService = ServiceFactory.getStudentService();
        } catch (RuntimeException exception) {
            logger.error("Error al inicializar StudentService: {}", exception.getMessage(), exception);
            statusLabel.setText("Error interno. Intente más tarde.");
            statusLabel.setTextFill(Color.RED);
            hasInitializationError = true;
        } catch (Exception exception) {
            logger.error("Error inesperado al inicializar StudentService: {}", exception.getMessage(), exception);
            statusLabel.setText("Error inesperado al inicializar.");
            statusLabel.setTextFill(Color.RED);
            hasInitializationError = true;
        }

        if (!hasInitializationError) {
            filterChoiceBox.getItems().addAll("Todos", "Mis estudiantes", "Activos", "Inactivos");
            filterChoiceBox.setValue("Todos");
            filterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadStudentData());

            setColumns();
            addDetailsButtonToTable();
            addManagementButtonToTable();
            loadStudentData();
            updateStudentCounts();
            setButtons();

            tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                selectedStudent = newValue;
                assignProjectButton.setDisable(selectedStudent == null);
                reassignProjectButton.setDisable(selectedStudent == null);
                deleteStudentButton.setDisable(selectedStudent == null);
                tableView.refresh();
            });
        }
    }

    public TableView<StudentDTO> getTableView() {
        return tableView;
    }

    private void setColumns() {
        tuitionColumn.setCellValueFactory(new PropertyValueFactory<>("tuition"));
        namesColumn.setCellValueFactory(new PropertyValueFactory<>("names"));
        surnamesColumn.setCellValueFactory(new PropertyValueFactory<>("surnames"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nrcColumn.setCellValueFactory(new PropertyValueFactory<>("NRC"));
    }

    private void setButtons() {
        searchButton.setOnAction(event -> searchStudent());
        registerStudentButton.setOnAction(event -> openRegisterStudentWindow());
        assignProjectButton.setOnAction(event -> openAssignProjectWindow());
        reassignProjectButton.setOnAction(event -> openReassignProjectWindow());
        deleteStudentButton.setOnAction(event -> handleDeleteStudent());
        setDisableButtons();
    }

    private void setDisableButtons() {
        assignProjectButton.setDisable(true);
        reassignProjectButton.setDisable(true);
        deleteStudentButton.setDisable(true);
    }

    private void setButtonVisibilityByRole(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    private void applyRoleRestrictions() {
        if (userRole == Role.EVALUATOR_ACADEMIC) {
            setButtonVisibilityByRole(registerStudentButton, false);
            setButtonVisibilityByRole(assignProjectButton, false);
            setButtonVisibilityByRole(reassignProjectButton, false);
            managementColumn.setVisible(false);
        } else if (userRole == Role.ACADEMIC) {
            setButtonVisibilityByRole(registerStudentButton, true);
            setButtonVisibilityByRole(assignProjectButton, false);
            setButtonVisibilityByRole(reassignProjectButton, false);
            managementColumn.setVisible(true);
        } else if (userRole == Role.COORDINATOR) {
            setButtonVisibilityByRole(registerStudentButton, false);
            setButtonVisibilityByRole(assignProjectButton, true);
            setButtonVisibilityByRole(reassignProjectButton, true);
            managementColumn.setVisible(true);
        }
    }

    private void openRegisterStudentWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterStudent.fxml"));
            Parent root = loader.load();

            GUI_RegisterStudentController registerController = loader.getController();
            registerController.setParentController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.error("No se pudo cargar el archivo FXML: {}", e.getMessage(), e);
            statusLabel.setText("Error al cargar el fxml al abrir la ventana de registro de estudiante.");
            statusLabel.setTextFill(Color.RED);
        } catch (NullPointerException e) {
            logger.error("No se encontró el recurso FXML: {}", e.getMessage(), e);
            statusLabel.setText("Recurso no encontrado al abrir la ventana de registro de estudiante.");
            statusLabel.setTextFill(Color.RED);
        } catch (Exception e) {
            logger.error("Error inesperado al abrir la ventana de registro de estudiante: {}", e.getMessage(), e);
            statusLabel.setText("Error inesperado al abrir la ventana de registro de estudiante.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void openAssignProjectWindow() {
        if (selectedStudent != null) {
            try {
                GUI_AssignProject.setStudent(selectedStudent);
                GUI_AssignProject assignProjectApp = new GUI_AssignProject();
                Stage stage = new Stage();
                assignProjectApp.start(stage);
            } catch (NullPointerException e) {
                logger.error("Recurso nulo al abrir la ventana: {}", e.getMessage(), e);
                statusLabel.setText("Error interno: recurso no encontrado");
                statusLabel.setTextFill(Color.RED);
            } catch (IllegalStateException e) {
                logger.error("Estado ilegal al abrir la ventana: {}", e.getMessage(), e);
                statusLabel.setText("Error de estado al abrir la ventana");
                statusLabel.setTextFill(Color.RED);
            } catch (Exception e) {
                logger.error("Error inesperado al abrir la ventana de asignación de proyecto: {}", e.getMessage(), e);
                statusLabel.setText("Error inesperado al abrir la ventana de asignación de proyecto");
                statusLabel.setTextFill(Color.RED);
            }
        } else {
            statusLabel.setText("Debe seleccionar un estudiante para asignar un proyecto");
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void openReassignProjectWindow() {
        if (selectedStudent != null) {
            try {
                StudentProjectDTO studentProjectDTO = new StudentProjectDAO().searchStudentProjectByIdTuiton(selectedStudent.getTuition());
                if (studentProjectDTO != null && studentProjectDTO.getIdProject() != null && !studentProjectDTO.getIdProject().isEmpty()) {
                    currentProject = new ProjectDAO().searchProjectById(studentProjectDTO.getIdProject());
                }
                gui.GUI_ReassignProject.setProjectStudent(selectedStudent, currentProject);
                gui.GUI_ReassignProject reassignProjectApp = new gui.GUI_ReassignProject();
                Stage stage = new Stage();
                reassignProjectApp.start(stage);
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if ("08001".equals(sqlState)) {
                    logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Error de conexión con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                } else if ("08S01".equals(sqlState)) {
                    logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Conexión interrumpida con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                } else if ("42S22".equals(sqlState)) {
                    logger.error("Columna desconocida en la tabla proyecto a estudiante base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Columna desconocida en la tabla proyecto a estudiante base de datos.");
                    statusLabel.setTextFill(Color.RED);
                } else if ("42S02".equals(sqlState)) {
                    logger.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Tabla desconocida en la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                } else if ("HY000".equals(sqlState)) {
                    logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Error de conexión con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                } else if ("42000".equals(sqlState)) {
                    logger.error("Base de datos desconocida: {}", e.getMessage(), e);
                    statusLabel.setText("Base de datos desconocida.");
                    statusLabel.setTextFill(Color.RED);
                } else if ("28000".equals(sqlState)) {
                    logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                } else {
                    logger.error("Error de base de datos al abrir la ventana de reasignación de proyecto: {}", e.getMessage(), e);
                    statusLabel.setText("Error de base de datos al abrir la ventana de reasignación de proyecto");
                    statusLabel.setTextFill(Color.RED);
                }
            } catch (NullPointerException e) {
                logger.error("Recurso nulo al abrir la ventana: {}", e.getMessage(), e);
                statusLabel.setText("Error interno: recurso no encontrado");
                statusLabel.setTextFill(Color.RED);
            } catch (IllegalStateException e) {
                logger.error("Estado ilegal al abrir la ventana: {}", e.getMessage(), e);
                statusLabel.setText("Error de estado al abrir la ventana");
                statusLabel.setTextFill(Color.RED);
            } catch (IOException e) {
                logger.error("Error al abrir el archivo de configuracion de la base de datos: {}", e.getMessage(), e);
                statusLabel.setText("Error al abrir el archivo de configuracion de la base de datos");
                statusLabel.setTextFill(Color.RED);
            } catch (Exception e) {
                logger.error("Error inesperado al abrir la ventana de reasignación de proyecto: {}", e.getMessage(), e);
                statusLabel.setText("Error inesperado al abrir la ventana de reasignación de proyecto");
                statusLabel.setTextFill(Color.RED);
            }
        } else {
            statusLabel.setText("Debe seleccionar un estudiante para reasignar proyecto");
            statusLabel.setTextFill(Color.RED);
        }
    }

    public void loadStudentData() {
        ObservableList<StudentDTO> studentList = FXCollections.observableArrayList();
        try {
            UserStudentViewDAO userStudentViewDAO = new UserStudentViewDAO();
            List<UserStudentViewDTO> userStudentViews = userStudentViewDAO.getAllUserStudentViews();
            String filter = filterChoiceBox != null ? filterChoiceBox.getValue() : "Todos";
            for (UserStudentViewDTO userStudentView : userStudentViews) {
                if (filterStudentByChoice(userStudentView, filter)) {
                    studentList.add(buildStudent(userStudentView));
                }
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al cargar los estudiantes.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de base de datos al cargar los estudiantes: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al cargar los estudiantes.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al cargar los estudiantes: {}", e.getMessage(), e);
        }
        tableView.setItems(studentList);
        updateStudentCounts();
    }

    private boolean filterStudentByChoice(UserStudentViewDTO userStudentView, String filter) {
        return switch (filter) {
            case "Todos" -> true;
            case "Activos" -> userStudentView.getStatus() == 1;
            case "Inactivos" -> userStudentView.getStatus() == 0;
            case "Mis estudiantes" -> userStudentView.getStatus() == 1 && userStudentView.getUserId() == idUserAcademic;
            default -> false;
        };
    }

    private StudentDTO buildStudent(UserStudentViewDTO userStudentView) {
        return new StudentDTO(
                userStudentView.getTuition(),
                userStudentView.getStatus(),
                userStudentView.getStudentNames(),
                userStudentView.getStudentSurnames(),
                userStudentView.getPhoneNumber(),
                userStudentView.getEmail(),
                userStudentView.getStudentUsername(),
                "",
                String.valueOf(userStudentView.getNrc()),
                userStudentView.getCreditProgress() != null ? String.valueOf(userStudentView.getCreditProgress()) : "",
                userStudentView.getFinalGrade() != null ? userStudentView.getFinalGrade().doubleValue() : 0.0
        );
    }

    private void searchStudent() {
        ObservableList<StudentDTO> filteredList = FXCollections.observableArrayList();
        String searchQuery = searchField.getText().trim();

        if (!searchQuery.isEmpty()) {
            try {
                UserStudentViewDAO userStudentViewDAO = new UserStudentViewDAO();
                UserStudentViewDTO userStudentView = userStudentViewDAO.getUserStudentViewByMatricula(searchQuery);
                if (userStudentView != null && userStudentView.getStatus() == 1 &&
                        (idUserAcademic == -1 || userStudentView.getUserId() == idUserAcademic)) {
                    filteredList.add(buildStudent(userStudentView));
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if ("08001".equals(sqlState)) {
                    statusLabel.setText("Error de conexión con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                } else if ("08S01".equals(sqlState)) {
                    statusLabel.setText("Conexión interrumpida con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                } else if ("42S22".equals(sqlState)) {
                    statusLabel.setText("Columna desconocida en la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
                } else if ("42S02".equals(sqlState)) {
                    statusLabel.setText("Tabla desconocida en la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
                } else if ("HY000".equals(sqlState)) {
                    statusLabel.setText("Error general de la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Error general de la base de datos: {}", e.getMessage(), e);
                } else if ("42000".equals(sqlState)) {
                    statusLabel.setText("Base de datos desconocida.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Base de datos desconocida: {}", e.getMessage(), e);
                } else if ("28000".equals(sqlState)) {
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                } else {
                    statusLabel.setText("Error de base de datos al buscar estudiante.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Error de base de datos al buscar estudiante: {}", e.getMessage(), e);
                }
            } catch (IOException e) {
                statusLabel.setText("Error al leer el archivo de configuración de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            } catch (Exception e) {
                statusLabel.setText("Error inesperado al buscar estudiante.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error inesperado al buscar estudiante: {}", e.getMessage(), e);
            }
        } else {
            loadStudentData();
            return;
        }

        tableView.setItems(filteredList);
    }

    private void updateStudentCounts() {
        try {
            UserStudentViewDAO userStudentViewDAO = new UserStudentViewDAO();
            List<UserStudentViewDTO> userStudentViews = userStudentViewDAO.getAllUserStudentViews();
            int total = 0;
            int active = 0;
            int inactive = 0;
            for (UserStudentViewDTO userStudentView : userStudentViews) {
                if (idUserAcademic == -1 || userStudentView.getUserId() == idUserAcademic) {
                    total++;
                    if (userStudentView.getStatus() == 1) {
                        active++;
                    } else {
                        inactive++;
                    }
                }
            }
            studentCountsLabel.setText("Totales: " + total + " | Activos: " + active + " | Inactivos: " + inactive);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                studentCountsLabel.setText("Error de conexión con la base de datos.");
                studentCountsLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                studentCountsLabel.setText("Conexión interrumpida con la base de datos.");
                studentCountsLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                studentCountsLabel.setText("Columna desconocida en la base de datos.");
                studentCountsLabel.setTextFill(Color.RED);
                logger.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                studentCountsLabel.setText("Tabla desconocida en la base de datos.");
                studentCountsLabel.setTextFill(Color.RED);
                logger.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                studentCountsLabel.setText("Error general de la base de datos.");
                studentCountsLabel.setTextFill(Color.RED);
                logger.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                studentCountsLabel.setText("Base de datos desconocida.");
                studentCountsLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                studentCountsLabel.setText("Acceso denegado a la base de datos.");
                studentCountsLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                studentCountsLabel.setText("Error de base de datos al actualizar conteos de estudiantes.");
                studentCountsLabel.setTextFill(Color.RED);
                logger.error("Error de base de datos al actualizar conteos de estudiantes: {}", e.getMessage(), e);
            }
        } catch (IOException e) {
            studentCountsLabel.setText("Error al leer el archivo de configuración de la base de datos.");
            studentCountsLabel.setTextFill(Color.RED);
            logger.error("Error al leer el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            studentCountsLabel.setText("Error inesperado al actualizar conteos de estudiantes.");
            studentCountsLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al actualizar conteos de estudiantes: {}", e.getMessage(), e);
        }
    }

    private void addDetailsButtonToTable() {
        Callback<TableColumn<StudentDTO, Void>, TableCell<StudentDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button detailsButton = new Button("Ver detalles");

            {
                detailsButton.setOnAction(event -> {
                    StudentDTO student = getTableView().getItems().get(getIndex());
                    openDetailsStudentWindow(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || selectedStudent == null || getTableView().getItems().get(getIndex()) != selectedStudent) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        };

        detailsColumn.setCellFactory(cellFactory);
    }

    private void openDetailsStudentWindow(StudentDTO student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_DetailsStudent.fxml"));
            Parent root = loader.load();
            GUI_DetailsStudentController controller = loader.getController();
            controller.setStudent(student);
            Stage stage = new Stage();
            stage.setTitle("Detalles del Estudiante");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Error de fxml al cargar la ventana de detalles del estudiante.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error de fxml al cargar la ventana de detalles del estudiante: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            statusLabel.setText("Recurso no encontrado al abrir la ventana de detalles del estudiante.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Recurso nulo al abrir la ventana de detalles: {}", e.getMessage(), e);
        } catch (IllegalStateException e) {
            statusLabel.setText("Error de estado al abrir la ventana de detalles del estudiante.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Estado ilegal al abrir la ventana de detalles: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al abrir la ventana de detalles del estudiante.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al abrir la ventana de detalles: {}", e.getMessage(), e);
        }
    }

    private void addManagementButtonToTable() {
        Callback<TableColumn<StudentDTO, Void>, TableCell<StudentDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button manageButton = new Button("Gestionar Estudiante");

            {
                manageButton.setOnAction(event -> {
                    StudentDTO student = getTableView().getItems().get(getIndex());
                    openManageStudentWindow(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || selectedStudent == null || getTableView().getItems().get(getIndex()) != selectedStudent) {
                    setGraphic(null);
                } else {
                    setGraphic(manageButton);
                }
            }
        };

        managementColumn.setCellFactory(cellFactory);
    }

    private void openManageStudentWindow(StudentDTO student) {
        ProjectDTO currentProject = null;
        try {
            StudentProjectDTO studentProjectDTO = new logic.DAO.StudentProjectDAO().searchStudentProjectByIdTuiton(student.getTuition());
            if (studentProjectDTO != null && studentProjectDTO.getIdProject() != null && !studentProjectDTO.getIdProject().isEmpty()) {
                currentProject = new logic.DAO.ProjectDAO().searchProjectById(studentProjectDTO.getIdProject());
            }
            GUI_ManageStudent.setStudent(student, currentProject);
            GUI_ManageStudent manageStudentApp = new GUI_ManageStudent();
            Stage stage = new Stage();
            manageStudentApp.start(stage);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if ("08001".equals(sqlState)) {
                statusLabel.setText("Error de conexión con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
            } else if ("08S01".equals(sqlState)) {
                statusLabel.setText("Conexión interrumpida con la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
            } else if ("42S22".equals(sqlState)) {
                statusLabel.setText("Columna desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
            } else if ("42S02".equals(sqlState)) {
                statusLabel.setText("Tabla desconocida en la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
            } else if ("HY000".equals(sqlState)) {
                statusLabel.setText("Error general de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error general de la base de datos: {}", e.getMessage(), e);
            } else if ("42000".equals(sqlState)) {
                statusLabel.setText("Base de datos desconocida.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Base de datos desconocida: {}", e.getMessage(), e);
            } else if ("28000".equals(sqlState)) {
                statusLabel.setText("Acceso denegado a la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
            } else {
                statusLabel.setText("Error de base de datos al abrir la ventana de gestión de estudiante.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error de base de datos al abrir la ventana de gestión de estudiante: {}", e.getMessage(), e);
            }
        } catch (NullPointerException e) {
            statusLabel.setText("Recurso no encontrado al abrir la ventana de gestión de estudiante.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Recurso nulo al abrir la ventana de gestión de estudiante: {}", e.getMessage(), e);
        } catch (IllegalStateException e) {
            statusLabel.setText("Error de estado al abrir la ventana de gestión de estudiante.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Estado ilegal al abrir la ventana de gestión de estudiante: {}", e.getMessage(), e);
        } catch (IOException e) {
            statusLabel.setText("Error al cargar el archivo de configuración de la base de datos.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error al cargar el archivo de configuración de la base de datos: {}", e.getMessage(), e);
        } catch (Exception e) {
            statusLabel.setText("Error inesperado al abrir la ventana de gestión de estudiante.");
            statusLabel.setTextFill(Color.RED);
            logger.error("Error inesperado al abrir la ventana de gestión de estudiante: {}", e.getMessage(), e);
        }
    }

    private void handleDeleteStudent() {
        boolean canDelete = true;

        if (selectedStudent == null) {
            statusLabel.setText("Debe seleccionar un estudiante para eliminar");
            canDelete = false;
        }

        if (canDelete) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ConfirmDialog.fxml"));
                Parent root = loader.load();
                GUI_ConfirmDialogController confirmController = loader.getController();
                confirmController.setInformationMessage("Al borrar un estudiante, se cambiará su estado a inactivo y no podrá realizar ninguna actividad.");
                confirmController.setConfirmMessage("¿Está seguro de que desea eliminar al estudiante " + selectedStudent.getNames() + " " + selectedStudent.getSurnames() + "?");
                Stage confirmStage = new Stage();
                confirmStage.setTitle("Confirmar Eliminación");
                confirmStage.setScene(new Scene(root));
                confirmStage.showAndWait();
                if (confirmController.isConfirmed()) {
                    studentService.updateStudentStatus(selectedStudent.getTuition(), 0);
                    statusLabel.setText("Estudiante eliminado correctamente.");
                    loadStudentData();
                    updateStudentCounts();
                } else {
                    statusLabel.setText("Eliminación cancelada.");
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if ("08001".equals(sqlState)) {
                    statusLabel.setText("Error de conexión con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Error de conexión con la base de datos: {}", e.getMessage(), e);
                } else if ("08S01".equals(sqlState)) {
                    statusLabel.setText("Conexión interrumpida con la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Conexión interrumpida con la base de datos: {}", e.getMessage(), e);
                } else if ("42S22".equals(sqlState)) {
                    statusLabel.setText("Columna desconocida en la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Columna desconocida en la base de datos: {}", e.getMessage(), e);
                } else if ("42S02".equals(sqlState)) {
                    statusLabel.setText("Tabla desconocida en la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Tabla desconocida en la base de datos: {}", e.getMessage(), e);
                } else if ("HY000".equals(sqlState)) {
                    statusLabel.setText("Error general de la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Error general de la base de datos: {}", e.getMessage(), e);
                } else if ("42000".equals(sqlState)) {
                    statusLabel.setText("Base de datos desconocida.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Base de datos desconocida: {}", e.getMessage(), e);
                } else if ("28000".equals(sqlState)) {
                    statusLabel.setText("Acceso denegado a la base de datos.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Acceso denegado a la base de datos: {}", e.getMessage(), e);
                } else {
                    statusLabel.setText("Error de base de datos al eliminar el estudiante.");
                    statusLabel.setTextFill(Color.RED);
                    logger.error("Error de base de datos al eliminar el estudiante: {}", e.getMessage(), e);
                }
            } catch (IOException e) {
                statusLabel.setText("Error al cargar el archivo de configuración de la base de datos.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error al cargar el archivo de configuración de la base de datos: {}", e.getMessage(), e);
            } catch (Exception e) {
                statusLabel.setText("Error inesperado al eliminar el estudiante.");
                statusLabel.setTextFill(Color.RED);
                logger.error("Error inesperado al eliminar el estudiante: {}", e.getMessage(), e);
            }
        }
    }
}