package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_DetailsPresentationStudentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;
import java.util.Date;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DetailsPresentationStudentControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;

    private int organizationId;
    private int departmentId;
    private int userId;
    private String projectId;
    private int presentationId;
    private String studentMatricula;
    private String criterionId;
    private int evaluationId;
    private int detailId;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_DetailsPresentationStudent.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Detalles de Presentación");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectToDatabase();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        insertMinimalDataForTest();
    }

    private void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
        }
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM detalle_evaluacion");
        statement.execute("DELETE FROM evaluacion_presentacion");
        statement.execute("DELETE FROM presentacion_proyecto");
        statement.execute("DELETE FROM proyecto");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM periodo");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM departamento");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("DELETE FROM criterio_de_evaluacion");
        statement.execute("ALTER TABLE detalle_evaluacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE evaluacion_presentacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void insertMinimalDataForTest() throws Exception {
        organizationId = insertTestOrganization();
        departmentId = insertTestDepartment(organizationId);
        userId = insertTestUser();
        String periodId = insertTestPeriod();
        int nrc = insertTestGroup(userId, periodId);
        studentMatricula = insertTestStudent(nrc);
        projectId = insertTestProject(userId, organizationId, departmentId);
        presentationId = insertTestPresentation(projectId);
        criterionId = insertTestAssessmentCriterion();
        evaluationId = insertTestEvaluationPresentation(presentationId, studentMatricula);
        detailId = insertTestEvaluationDetail(evaluationId, criterionId);
    }

    private int insertTestOrganization() throws SQLException {
        String sqlQuery = "INSERT INTO organizacion_vinculada (nombre, direccion, estado) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Org Test");
            statement.setString(2, "Dirección Test");
            statement.setInt(3, 1);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar organización");
    }

    private int insertTestDepartment(int orgId) throws SQLException {
        String sqlQuery = "INSERT INTO departamento (nombre, descripcion, idOrganizacion, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Dept Test");
            statement.setString(2, "Descripción test");
            statement.setInt(3, orgId);
            statement.setInt(4, 1);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar departamento");
    }

    private int insertTestUser() throws SQLException {
        String sqlQuery = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "12345");
            statement.setString(2, "Nombre");
            statement.setString(3, "Apellido");
            statement.setString(4, "usuarioTest");
            statement.setString(5, "passTest");
            statement.setString(6, Role.ACADEMIC.getDataBaseValue());
            statement.setInt(7, 1);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar usuario");
    }

    private String insertTestPeriod() throws SQLException {
        String periodId = "20241";
        String sqlQuery = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, periodId);
            statement.setString(2, "2024-1");
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis() + 1000000));
            statement.executeUpdate();
        }
        return periodId;
    }

    private int insertTestGroup(int userId, String periodId) throws SQLException {
        int nrc = 12345;
        String sqlQuery = "INSERT INTO grupo (nrc, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, nrc);
            statement.setString(2, "Grupo Test");
            statement.setInt(3, userId);
            statement.setString(4, periodId);
            statement.executeUpdate();
        }
        return nrc;
    }

    private String insertTestStudent(int nrc) throws SQLException {
        String tuition = "A12345678";
        String sqlQuery = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, nrc, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, tuition);
            statement.setInt(2, 1);
            statement.setString(3, "Estudiante");
            statement.setString(4, "Apellido");
            statement.setString(5, "1234567890");
            statement.setString(6, "correo@test.com");
            statement.setString(7, "test");
            statement.setString(8, "test");
            statement.setInt(9, nrc);
            statement.setString(10, "100");
            statement.setDouble(11, 0.0);
            statement.executeUpdate();
        }
        return tuition;
    }

    private String insertTestProject(int userId, int orgId, int deptId) throws SQLException {
        String sqlQuery = "INSERT INTO proyecto (nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion, idDepartamento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Proyecto Test");
            statement.setString(2, "Descripción Test");
            statement.setDate(3, new java.sql.Date(System.currentTimeMillis() + 1000000));
            statement.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            statement.setInt(5, userId);
            statement.setInt(6, orgId);
            statement.setInt(7, deptId);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return String.valueOf(resultSet.getInt(1));
            }
        }
        throw new SQLException("No se pudo insertar proyecto");
    }

    private int insertTestPresentation(String projectId) throws SQLException {
        String sqlQuery = "INSERT INTO presentacion_proyecto (idProyecto, fecha, tipo) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, projectId);
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            statement.setString(3, "Parcial");
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar presentación");
    }

    private String insertTestAssessmentCriterion() throws SQLException {
        String criterionId = String.valueOf((int) (Math.random() * 100000));
        String sqlQuery = "INSERT INTO criterio_de_evaluacion (idCriterio, nombreCriterio) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, criterionId);
            statement.setString(2, "Criterio Test");
            statement.executeUpdate();
        }
        return criterionId;
    }

    private int insertTestEvaluationPresentation(int presentationId, String matricula) throws SQLException {
        String sqlQuery = "INSERT INTO evaluacion_presentacion (idPresentacion, matricula, fecha, comentario, promedio) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, presentationId);
            statement.setString(2, matricula);
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            statement.setString(4, "Comentario Test");
            statement.setDouble(5, 9.0);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar evaluación de presentación");
    }

    private int insertTestEvaluationDetail(int evaluationId, String criterionId) throws SQLException {
        String sqlQuery = "INSERT INTO detalle_evaluacion (idEvaluacion, idCriterio, calificacion) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, evaluationId);
            statement.setString(2, criterionId);
            statement.setDouble(3, 9.0);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar detalle de evaluación");
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
        clearTablesAndResetAutoIncrement();
    }

    @Test
    public void testLoadDetailsForEvaluation() {
        WaitForAsyncUtils.waitForFxEvents();
        GUI_DetailsPresentationStudentController controller = loader.getController();
        interact(() -> controller.setIdEvaluation(evaluationId));
        WaitForAsyncUtils.waitForFxEvents();

        VBox detailsVBox = lookup("#detailsVBox").query();
        Label statusLabel = lookup("#statusLabel").query();

        assertThat(detailsVBox.getChildren()).isNotEmpty();
        assertThat(statusLabel.getText()).isEmpty();
    }

    @Test
    public void testLoadDetailsForNonExistentEvaluation() {
        WaitForAsyncUtils.waitForFxEvents();
        GUI_DetailsPresentationStudentController controller = loader.getController();
        interact(() -> controller.setIdEvaluation(999999));
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("No hay detalles");
    }
}