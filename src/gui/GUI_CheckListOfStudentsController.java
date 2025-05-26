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
import logic.DTO.StudentDTO;
import logic.services.ServiceFactory;
import logic.services.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private Label statusLabel;

    private StudentDTO selectedStudent;
    private StudentService studentService;

    public void initialize() {
        try {
            this.studentService = ServiceFactory.getStudentService();
        } catch (RuntimeException e) {
            logger.error("Error al inicializar StudentService: {}", e.getMessage(), e);
            statusLabel.setText("Error interno. Intente más tarde.");
            return;
        }

        columnTuiton.setCellValueFactory(new PropertyValueFactory<>("tuiton"));
        columnNames.setCellValueFactory(new PropertyValueFactory<>("names"));
        columnSurnames.setCellValueFactory(new PropertyValueFactory<>("surnames"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnnNRC.setCellValueFactory(new PropertyValueFactory<>("NRC"));

        addDetailsButtonToTable();
        addManagementButtonToTable();

        loadStudentData();

        searchButton.setOnAction(event -> searchStudent());
        buttonRegisterStudent.setOnAction(event -> openRegisterStudentWindow());
        buttonAssignProject.setOnAction(event -> openAssignProjectWindow());

        buttonAssignProject.setDisable(true);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedStudent = newValue;
            buttonAssignProject.setDisable(selectedStudent == null);
            tableView.refresh();
        });
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
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de registro de estudiante: {}", e.getMessage(), e);
        }
    }

    private void openAssignProjectWindow() {
        if (selectedStudent == null) {
            statusLabel.setText("Debe seleccionar un estudiante para asignar un proyecto");
            return;
        }

        try {
            GUI_AssignProject.setStudent(selectedStudent);
            GUI_AssignProject assignProjectApp = new GUI_AssignProject();
            Stage stage = new Stage();
            assignProjectApp.start(stage);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de asignación de proyecto: {}", e.getMessage(), e);
            statusLabel.setText("Error al abrir la ventana de asignación de proyecto");
        }
    }

    public void loadStudentData() {
        ObservableList<StudentDTO> studentList = FXCollections.observableArrayList();

        try {
            List<StudentDTO> students = studentService.getAllStudents();
            studentList.addAll(students);
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Error al cargar los datos de los estudiantes.");
            logger.error("Error al cargar los datos de los estudiantes: {}", e.getMessage(), e);
        }

        tableView.setItems(studentList);
    }

    private void searchStudent() {
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

    private void addDetailsButtonToTable() {
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

        columnManagement.setCellFactory(cellFactory);
    }

    private void openManageStudentWindow(StudentDTO student) {
        try {
            GUI_ManageStudent.setStudent(student);
            GUI_ManageStudent manageStudentApp = new GUI_ManageStudent();
            Stage stage = new Stage();
            manageStudentApp.start(stage);
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de gestión de estudiante: {}", e.getMessage(), e);
        }
    }
}