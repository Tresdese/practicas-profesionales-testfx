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

import logic.DTO.ProjectDTO;
import logic.DAO.ProjectDAO;
import logic.DTO.StudentDTO;
import logic.DTO.StudentProjectDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import logic.DTO.Role;

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
    private TableColumn<StudentDTO, String> columnTuiton;

    @FXML
    private TableColumn<StudentDTO, String> columnNames;

    @FXML
    private TableColumn<StudentDTO, String> columnSurnames;

    @FXML
    private TableColumn<StudentDTO, String> columnEmail;

    @FXML
    private TableColumn<StudentDTO, String> columnnNRC;

    @FXML
    private TableColumn<StudentDTO, Void> columnDetails;

    @FXML
    private TableColumn<StudentDTO, Void> columnManagement;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button buttonRegisterStudent;

    @FXML
    private Button buttonAssignProject;

    @FXML
    private Button buttonReassignProject;

    @FXML
    private Label statusLabel;

    private StudentDTO selectedStudent;
    private StudentService studentService;
    private StudentProjectDTO studentProject;
    private ProjectDTO currentProject;
    private Role userRole;

    public void initialize() {
        try {
            this.studentService = ServiceFactory.getStudentService();
        } catch (RuntimeException e) {
            logger.error("Error al inicializar StudentService: {}", e.getMessage(), e);
            statusLabel.setText("Error interno. Intente más tarde.");
            return;
        }

        setColumns();

        addDetailsButtonToTable();
        addManagementButtonToTable();

        loadStudentData();

        searchButton.setOnAction(event -> searchStudent());
        buttonRegisterStudent.setOnAction(event -> openRegisterStudentWindow());
        buttonAssignProject.setOnAction(event -> openAssignProjectWindow());
        buttonReassignProject.setOnAction(event -> openReassignProjectWindow());

        buttonAssignProject.setDisable(true);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedStudent = newValue;
            buttonAssignProject.setDisable(selectedStudent == null);
            buttonReassignProject.setDisable(selectedStudent == null);
            tableView.refresh();
        });
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
        applyRolRestrictions();
    }

    public void setButtonVisibility(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    public void setColumns () {
        columnTuiton.setCellValueFactory(new PropertyValueFactory<>("tuiton"));
        columnNames.setCellValueFactory(new PropertyValueFactory<>("names"));
        columnSurnames.setCellValueFactory(new PropertyValueFactory<>("surnames"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnnNRC.setCellValueFactory(new PropertyValueFactory<>("NRC"));
    }

    public void applyRolRestrictions() {
        if (userRole == Role.ACADEMICO_EVALUADOR) {
            setButtonVisibility(buttonRegisterStudent, false);
            setButtonVisibility(buttonAssignProject, false);
            setButtonVisibility(buttonReassignProject, false);
            columnManagement.setVisible(false);
        } else if (userRole == Role.ACADEMICO) {
            setButtonVisibility(buttonRegisterStudent, true);
            setButtonVisibility(buttonAssignProject, false);
            setButtonVisibility(buttonReassignProject, false);
            columnManagement.setVisible(true);
        } else if (userRole == Role.COORDINADOR) {
            setButtonVisibility(buttonRegisterStudent, false);
            setButtonVisibility(buttonAssignProject, true);
            setButtonVisibility(buttonReassignProject, true);
            columnManagement.setVisible(false);
        }
    }

    public void openRegisterStudentWindow() {
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
        } catch (NullPointerException e) {
            logger.error("No se encontró el recurso FXML: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de registro de estudiante: {}", e.getMessage(), e);
        }
    }

    public void openAssignProjectWindow() {
        if (selectedStudent == null) {
            statusLabel.setText("Debe seleccionar un estudiante para asignar un proyecto");
            return;
        }

        try {
            GUI_AssignProject.setStudent(selectedStudent);
            GUI_AssignProject assignProjectApp = new GUI_AssignProject();
            Stage stage = new Stage();
            assignProjectApp.start(stage);
        } catch (NullPointerException e) {
            logger.error("Recurso nulo al abrir la ventana: {}", e.getMessage(), e);
            statusLabel.setText("Error interno: recurso no encontrado");
        } catch (IllegalStateException e) {
            logger.error("Estado ilegal al abrir la ventana: {}", e.getMessage(), e);
            statusLabel.setText("Error de estado al abrir la ventana");
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de asignación de proyecto: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de asignación de proyecto");
        }
    }

    public void openReassignProjectWindow() {
        if (selectedStudent == null) {
            statusLabel.setText("Debe seleccionar un estudiante para reasignar proyecto");
            return;
        }
        ProjectDTO currentProject = null;
        try {
            StudentProjectDTO studentProjectDTO = new logic.DAO.StudentProjectDAO().searchStudentProjectByIdTuiton(selectedStudent.getTuiton());
            if (studentProjectDTO != null && studentProjectDTO.getIdProject() != null && !studentProjectDTO.getIdProject().isEmpty()) {
                currentProject = new logic.DAO.ProjectDAO().searchProjectById(studentProjectDTO.getIdProject());
            }
            gui.GUI_ReassignProject.setProjectStudent(selectedStudent, currentProject);
            gui.GUI_ReassignProject reassignProjectApp = new gui.GUI_ReassignProject();
            Stage stage = new Stage();
            reassignProjectApp.start(stage);
        } catch (SQLException e) {
            logger.error("Error de base de datos al reasignar proyecto: {}", e.getMessage(), e);
            statusLabel.setText("Error de base de datos al reasignar proyecto");
        } catch (NullPointerException e) {
            logger.error("Recurso nulo al abrir la ventana: {}", e.getMessage(), e);
            statusLabel.setText("Error interno: recurso no encontrado");
        } catch (IllegalStateException e) {
            logger.error("Estado ilegal al abrir la ventana: {}", e.getMessage(), e);
            statusLabel.setText("Error de estado al abrir la ventana");
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de reasignación de proyecto: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de reasignación de proyecto");
        }
    }

    public void loadStudentData() {
        ObservableList<StudentDTO> studentList = FXCollections.observableArrayList();

        try {
            List<StudentDTO> students = studentService.getAllStudents();
            for (StudentDTO student : students) {
                if (student.getState() == 1) {
                    studentList.add(student);
                }
            }
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de los estudiantes.");
            logger.error("Error al cargar los datos de los estudiantes: {}", e.getMessage(), e);
        }

        tableView.setItems(studentList);
    }

    public void searchStudent() {
        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadStudentData();
            return;
        }

        ObservableList<StudentDTO> filteredList = FXCollections.observableArrayList();

        try {
            StudentDTO student = studentService.searchStudentByTuiton(searchQuery);
            if (student != null) {
                filteredList.add(student);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error al buscar el estudiante.");
            logger.error("Error al buscar el estudiante: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
    }

    public void addDetailsButtonToTable() {
        Callback<TableColumn<StudentDTO, Void>, TableCell<StudentDTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button detailsButton = new Button("Ver detalles");

            {
                detailsButton.setOnAction(event -> {
                    StudentDTO student = getTableView().getItems().get(getIndex());
                    System.out.println("Detalles de: " + student.getTuiton());
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

        columnDetails.setCellFactory(cellFactory);
    }

    public void addManagementButtonToTable() {
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

        columnManagement.setCellFactory(cellFactory);
    }

    public void openManageStudentWindow(StudentDTO student) {
        ProjectDTO currentProject = null;
        try {
            StudentProjectDTO studentProjectDTO = new logic.DAO.StudentProjectDAO().searchStudentProjectByIdTuiton(student.getTuiton());
            if (studentProjectDTO != null && studentProjectDTO.getIdProject() != null && !studentProjectDTO.getIdProject().isEmpty()) {
                currentProject = new logic.DAO.ProjectDAO().searchProjectById(studentProjectDTO.getIdProject());
            }
            GUI_ManageStudent.setStudent(student, currentProject);
            GUI_ManageStudent manageStudentApp = new GUI_ManageStudent();
            Stage stage = new Stage();
            manageStudentApp.start(stage);
        } catch (SQLException e) {
            logger.error("Error de base de datos al gestionar estudiante: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            logger.error("Recurso nulo al abrir la ventana de gestión de estudiante: {}", e.getMessage(), e);
        } catch (IllegalStateException e) {
            logger.error("Estado ilegal al abrir la ventana de gestión de estudiante: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de gestión de estudiante: {}", e.getMessage(), e);
        }
    }
}