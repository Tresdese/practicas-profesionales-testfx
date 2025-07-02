package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_ManageProjectRequestController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.ProjectRequestDAO;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.ProjectStatus;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ManageProjectRequestControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;
    private ProjectRequestDTO testRequest;
    private ProjectRequestDAO projectRequestDAO;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageProjectRequest.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Gestión de Solicitud de Proyecto");
        stage.setScene(scene);
        stage.show();
    }

    void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
        }
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectToDatabase();
        clearTable();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTable();
        createTestProjectRequest();
        interact(this::setProjectRequestDataInController);
    }

    private void clearTable() throws SQLException {
        try (PreparedStatement disableForeignKey = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
            disableForeignKey.execute();
        }
        connection.prepareStatement("DELETE FROM solicitud_proyecto").executeUpdate();
        connection.prepareStatement("DELETE FROM estudiante").executeUpdate();
        connection.prepareStatement("DELETE FROM grupo").executeUpdate();
        connection.prepareStatement("DELETE FROM periodo").executeUpdate();
        connection.prepareStatement("DELETE FROM proyecto").executeUpdate();
        connection.prepareStatement("DELETE FROM usuario").executeUpdate();
        connection.prepareStatement("DELETE FROM organizacion_vinculada").executeUpdate();
        connection.prepareStatement("DELETE FROM representante").executeUpdate();
        // Reinicia el autoincremento para evitar IDs inesperados
        connection.prepareStatement("ALTER TABLE solicitud_proyecto AUTO_INCREMENT = 1").executeUpdate();
        try (PreparedStatement enableForeignKey = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
            enableForeignKey.execute();
        }
    }

    private void createTestProjectRequest() throws SQLException, IOException {
        createTestRepresentative();
        createTestStudent();
        testRequest = new ProjectRequestDTO(
                0,
                "S12345678", "1", "1", "Proyecto Test", "Descripción", "Objetivo general",
                "Inmediatos", "Mediatos", "Metodología", "Recursos", "Actividades", "Responsabilidades",
                10, "Lunes,Martes", 5, 10, ProjectStatus.pending, null
        );
        projectRequestDAO = new ProjectRequestDAO();
        projectRequestDAO.insertProjectRequest(testRequest);
        List<ProjectRequestDTO> requests = projectRequestDAO.getProjectRequestsByTuiton("S12345678");
        if (!requests.isEmpty()) {
            testRequest = requests.get(0);
        }
    }

    private void setProjectRequestDataInController() {
        GUI_ManageProjectRequestController controller = loader.getController();
        controller.setProjectRequestDAO(projectRequestDAO);
        controller.setProjectRequestData(testRequest);
    }

    private void createTestStudent() throws SQLException {
        createTestGroup();
        String sql = "INSERT IGNORE INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, nrc, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "S12345678");
            stmt.setInt(2, 1);
            stmt.setString(3, "Juan");
            stmt.setString(4, "Pérez");
            stmt.setString(5, "1234567890");
            stmt.setString(6, "juan.perez@example.com");
            stmt.setString(7, "juanperez");
            stmt.setString(8, "password");
            stmt.setString(9, "11111");
            stmt.setString(10, "50");
            stmt.setDouble(11, 0.0);
            stmt.executeUpdate();
        }
    }

    private void createTestGroup() throws SQLException {
        createTestPeriod();
        String sql = "INSERT IGNORE INTO grupo (NRC, nombre, idPeriodo) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "11111");
            stmt.setString(2, "Grupo Test");
            stmt.setString(3, "1001");
            stmt.executeUpdate();
        }
    }

    private void createTestPeriod() throws SQLException {
        String sql = "INSERT IGNORE INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, NOW(), NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "1001");
            stmt.setString(2, "Periodo Test");
            stmt.executeUpdate();
        }
    }

    private void createTestOrganization() throws SQLException {
        String sql = "INSERT IGNORE INTO organizacion_vinculada (idOrganizacion, nombre, direccion, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "Org Test");
            stmt.setString(3, "Dirección Test");
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
    }

    private void createTestDepartment() throws SQLException {
        createTestOrganization();
        String sql = "INSERT IGNORE INTO departamento (idDepartamento, nombre, descripcion, idOrganizacion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "Dept Test");
            stmt.setString(3, "Descripción test");
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
    }

    private void createTestRepresentative() throws SQLException {
        createTestDepartment();
        String sql = "INSERT IGNORE INTO representante (idRepresentante, nombres, apellidos, correo, idOrganizacion, idDepartamento, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "Nombre Rep");
            stmt.setString(3, "Apellido Rep");
            stmt.setString(4, "rep@example.com");
            stmt.setInt(5, 1);
            stmt.setInt(6, 1);
            stmt.setInt(7, 1);
            stmt.executeUpdate();
        }
    }

    @AfterAll
    void tearDownAll() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        clearTable();
    }

    @Test
    public void testEditAndSaveProjectRequest() throws Exception {
        WaitForAsyncUtils.waitForFxEvents();

        TextArea descriptionField = lookup("#descriptionField").query();
        TextField durationField = lookup("#durationField").query();
        ComboBox<String> statusCombo = lookup("#statusCombo").query();
        Button saveButton = lookup(".button")
                .queryAll()
                .stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Guardar"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        interact(() -> {
            descriptionField.setText("Descripción modificada");
            durationField.setText("20");
            statusCombo.setValue("Aprobado");
        });

        clickOn(saveButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        ProjectRequestDTO updated = projectRequestDAO.searchProjectRequestById(testRequest.getRequestId());

        assertThat(statusLabel.getText()).contains("¡Solicitud de proyecto actualizada exitosamente!");
        assertThat(updated.getDescription()).isEqualTo("Descripción modificada");
        assertThat(updated.getDuration()).isEqualTo(20);
        assertThat(updated.getStatus()).isEqualTo(ProjectStatus.approved);
    }

    @Test
    public void testSaveButtonDisabledInitially() {
        WaitForAsyncUtils.waitForFxEvents();
        Button saveButton = lookup(".button")
                .queryAll()
                .stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Guardar"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        assertThat(saveButton.isDisabled()).isTrue();
    }

    @Test
    public void testStatusComboBoxContainsAllStatuses() {
        WaitForAsyncUtils.waitForFxEvents();
        ComboBox<String> statusCombo = lookup("#statusCombo").query();

        assertThat(statusCombo.getItems()).containsExactlyInAnyOrder(
                "Pendiente", "Aprobado", "Rechazado");
    }
}