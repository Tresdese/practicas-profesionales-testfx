package user_interface.controllers;

import data_access.ConnectionDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.DAO.ProjectDAO;
import logic.DTO.ProjectDTO;
import logic.DTO.Type;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterPresentationControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private ProjectDAO projectDAO;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterPresentation.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Registrar presentación");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectToDatabase();
        setServices();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        insertTestProjectIfNeeded();
    }

    private void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
        }
    }

    private void setServices() {
        projectDAO = new ProjectDAO();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM presentacion_proyecto");
        statement.execute("DELETE FROM proyecto");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM departamento");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private int insertTestOrganization() throws SQLException {
        String sql = "INSERT INTO organizacion_vinculada (nombre, direccion, estado) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Org Test");
            statement.setString(2, "Dirección Test");
            statement.setInt(3, 1);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar organización");
    }

    private int insertTestDepartment(int orgId) throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Dept test");
            statement.setString(2, "Description test");
            statement.setInt(3, orgId);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar departamento");
    }

    private int insertTestUser() throws SQLException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "12345");
            statement.setString(2, "Nombre");
            statement.setString(3, "Apellido");
            statement.setString(4, "usuarioTest");
            statement.setString(5, "passTest");
            statement.setString(6, "ACADEMICO");
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar usuario");
    }

    private void insertTestProjectIfNeeded() throws SQLException, IOException {
        List<ProjectDTO> projects = projectDAO.getAllProjects();
        if (projects.isEmpty()) {
            int orgId = insertTestOrganization();
            int deptId = insertTestDepartment(orgId);
            int userId = insertTestUser();
            ProjectDTO project = new ProjectDTO(
                    null,
                    "Proyecto Test",
                    "Descripción",
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    new java.sql.Timestamp(System.currentTimeMillis()),
                    String.valueOf(userId),
                    orgId,
                    deptId
            );
            projectDAO.insertProject(project);
        }
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    public void testSuccessPresentationRegister() throws Exception {
        ProjectDTO project = projectDAO.getAllProjects().get(0);

        interact(() -> {
            ComboBox<ProjectDTO> projectBox = lookup("#idProjectComboBox").query();
            projectBox.getSelectionModel().select(project);

            DatePicker datePicker = lookup("#dateField").query();
            datePicker.setValue(LocalDate.of(2024, 6, 1));

            TextField timeField = lookup("#timeField").query();
            timeField.setText("10:00");

            ComboBox<Type> typeBox = lookup("#typeComboBox").query();
            typeBox.getSelectionModel().select(Type.Partial);
        });

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Presentación registrada correctamente.");
    }

    @Test
    public void testFailurePresentationRegisterWithoutAllFields() {
        interact(() -> {
            ComboBox<ProjectDTO> projectBox = lookup("#idProjectComboBox").query();
            projectBox.getSelectionModel().clearSelection();

            DatePicker datePicker = lookup("#dateField").query();
            datePicker.setValue(LocalDate.of(2024, 6, 1));

            TextField timeField = lookup("#timeField").query();
            timeField.setText("10:00");

            ComboBox<Type> typeBox = lookup("#typeComboBox").query();
            typeBox.getSelectionModel().select(Type.Partial);
        });

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos son obligatorios.");
    }
}