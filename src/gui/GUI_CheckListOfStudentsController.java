package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.DAO.StudentDAO;
import logic.DTO.StudentDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
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
    private TextField searchField;

    @FXML
    private Button searchButton;

    public void initialize() {
        columnTuiton.setCellValueFactory(new PropertyValueFactory<>("tuiton"));
        columnNames.setCellValueFactory(new PropertyValueFactory<>("names"));
        columnSurnames.setCellValueFactory(new PropertyValueFactory<>("surnames"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnnNRC.setCellValueFactory(new PropertyValueFactory<>("NRC"));

        loadStudentData();

        searchButton.setOnAction(event -> searchStudent());
    }

    private void loadStudentData() {
        ObservableList<StudentDTO> studentList = FXCollections.observableArrayList();
        StudentDAO studentDAO = new StudentDAO();

        try (Connection connection = new data_access.ConecctionDataBase().connectDB()) {
            List<StudentDTO> students = studentDAO.getAllStudents(connection);
            studentList.addAll(students);
        } catch (SQLException e) {
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
        StudentDAO studentDAO = new StudentDAO();

        try (Connection connection = new data_access.ConecctionDataBase().connectDB()) {
            StudentDTO student = studentDAO.getStudent(searchQuery, connection);
            if (student != null) {
                filteredList.add(student);
            }
        } catch (SQLException e) {
            logger.error("Error al buscar el estudiante: {}", e.getMessage(), e);
        }

        tableView.setItems(filteredList);
    }
}