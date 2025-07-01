package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckListOfReportsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DTO.ReportDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckListOfReportsControllerTest extends ApplicationTest {

    private static final String TEST_NRC = "12345";

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;
    private final String testTuition = "A12345678";
    private int testEvidenceId;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfReports.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de reportes");
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
        clearTables();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTables();
        createTestPeriod();
        int organizationId = createTestOrganization();
        int departmentId = createTestDepartment(organizationId);
        int userId = createTestUser();
        int projectId = createTestProject(organizationId, departmentId, userId);
        createTestGroup(userId);
        createTestStudent();
        createTestEvidence();
        createTestReport(projectId);
    }

    private void createTestPeriod() throws SQLException {
        String sql = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "1");
            statement.setString(2, "2024-1");
            statement.setTimestamp(3, Timestamp.valueOf("2024-01-01 00:00:00"));
            statement.setTimestamp(4, Timestamp.valueOf("2024-06-30 23:59:59"));
            statement.executeUpdate();
        }
    }

    private void createTestGroup(int userId) throws SQLException {
        String sql = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, TEST_NRC);
            statement.setString(2, "Grupo Prueba");
            statement.setInt(3, userId);
            statement.setString(4, "1");
            statement.executeUpdate();
        }
    }

    private void clearTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM periodo");
        statement.execute("DELETE FROM proyecto");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("DELETE FROM departamento");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM reporte");
        statement.execute("DELETE FROM evidencia");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private int createTestOrganization() throws SQLException {
        String sql = "INSERT INTO organizacion_vinculada (nombre, direccion, estado) VALUES (?, ?, 1)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Org Prueba");
            statement.setString(2, "Calle Falsa 123");
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo crear la organización");
    }

    private int createTestDepartment(int organizationId) throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion, estado) VALUES (?, ?, ?, 1)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Depto Prueba");
            statement.setString(2, "Depto Desc");
            statement.setInt(3, organizationId);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo crear el departamento");
    }

    private int createTestProject(int organizationId, int departmentId, int userId) throws SQLException {
        String sql = "INSERT INTO proyecto (nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion, idDepartamento) VALUES (?, ?, NOW(), NOW(), ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Proyecto Prueba");
            statement.setString(2, "Desc Prueba");
            statement.setInt(3, userId);
            statement.setInt(4, organizationId);
            statement.setInt(5, departmentId);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo crear el proyecto");
    }

    private void createTestStudent() throws SQLException {
        String sql = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, testTuition);
            statement.setInt(2, 1);
            statement.setString(3, "Estudiante");
            statement.setString(4, "Prueba");
            statement.setString(5, "1234567890");
            statement.setString(6, "estudiante@prueba.com");
            statement.setString(7, "estuprueba");
            statement.setString(8, "passTest");
            statement.setString(9, TEST_NRC);
            statement.setString(10, "100");
            statement.setDouble(11, 10.0);
            statement.executeUpdate();
        }
    }

    private void createTestEvidence() throws SQLException {
        String sql = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Evidencia de prueba");
            statement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            statement.setString(3, "ruta/a/la/evidencia");
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    testEvidenceId = rs.getInt(1);
                }
            }
        }
    }

    private void createTestReport(int projectId) throws SQLException {
        String sql = "INSERT INTO reporte (fecha_reporte, total_horas, objetivo_general, metodologia, resultado_obtenido, idProyecto, matricula, observaciones, idEvidencia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
            statement.setInt(2, 40);
            statement.setString(3, "Objetivo de prueba");
            statement.setString(4, "Metodología de prueba");
            statement.setString(5, "Resultado obtenido de prueba");
            statement.setInt(6, projectId);
            statement.setString(7, testTuition);
            statement.setString(8, "Observaciones de prueba");
            statement.setInt(9, testEvidenceId);
            statement.executeUpdate();
        }
    }

    private int createTestUser() throws SQLException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "12345");
            statement.setString(2, "Juan");
            statement.setString(3, "Pérez");
            statement.setString(4, "juanperez");
            statement.setString(5, "passTest");
            statement.setString(6, "ACADEMICO");
            statement.setInt(7, 1);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo crear el usuario");
    }

    private void forceLoadData() {
        GUI_CheckListOfReportsController controller = loader.getController();
        interact(() -> controller.setStudentTuition(testTuition));
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
        clearTables();
    }

    @Test
    public void testLoadReportsPopulatesTable() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ReportDTO> tableView = lookup("#reportsTableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        ReportDTO report = tableView.getItems().get(0);
        assertThat(report.getNumberReport()).isNotNull();
        assertThat(report.getNumberReport()).isNotEmpty();
        assertThat(report.getTuition()).isEqualTo(testTuition);
        assertThat(report.getGeneralObjective()).isEqualTo("Objetivo de prueba");
    }

    @Test
    public void testLoadReportsTableEmptyWhenNoReports() throws Exception {
        clearTables();
        GUI_CheckListOfReportsController controller = loader.getController();
        interact(() -> controller.setStudentTuition(testTuition));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<ReportDTO> tableView = lookup("#reportsTableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }
}