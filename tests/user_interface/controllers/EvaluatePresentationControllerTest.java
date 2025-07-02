package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_EvaluatePresentationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.DTO.*;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EvaluatePresentationControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;

    private int userId;
    private int organizationId;
    private int departmentId;
    private String projectId;
    private String studentMatricula;
    private int periodId;
    private int nrc;
    private int presentationId;
    private int criterionId;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_EvaluatePresentation.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Evaluar Presentación");
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
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE evaluacion_presentacion");
        statement.execute("TRUNCATE TABLE detalle_evaluacion");
        statement.execute("TRUNCATE TABLE presentacion_proyecto");
        statement.execute("TRUNCATE TABLE criterio_de_evaluacion");
        statement.execute("TRUNCATE TABLE proyecto");
        statement.execute("TRUNCATE TABLE estudiante");
        statement.execute("TRUNCATE TABLE grupo");
        statement.execute("TRUNCATE TABLE periodo");
        statement.execute("TRUNCATE TABLE usuario");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("ALTER TABLE evaluacion_presentacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE detalle_evaluacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE criterio_de_evaluacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createBaseData() throws SQLException {

        String orgSql = "INSERT INTO organizacion_vinculada (nombre, direccion, estado) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(orgSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Org Test");
            statement.setString(2, "Dirección Test");
            statement.setInt(3, 1);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) organizationId = resultSet.getInt(1);
            }
        }

        String userSql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "12345");
            statement.setString(2, "Nombre");
            statement.setString(3, "Apellido");
            statement.setString(4, "usuarioTest");
            statement.setString(5, "passTest");
            statement.setString(6, Role.ACADEMIC.getDataBaseValue());
            statement.setInt(7, 1);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) userId = resultSet.getInt(1);
            }
        }

        String departmentSql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(departmentSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Dept Test");
            statement.setString(2, "Desc");
            statement.setInt(3, organizationId);
            statement.setInt(4, 1);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) departmentId = resultSet.getInt(1);
            }
        }

        periodId = 20241;
        String periodSql = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(periodSql)) {
            statement.setString(1, String.valueOf(periodId));
            statement.setString(2, "2024-1");
            statement.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis() + 1000000));
            statement.executeUpdate();
        }

        nrc = 12345;
        String groupSql = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(groupSql)) {
            statement.setString(1, String.valueOf(nrc));
            statement.setString(2, "Grupo Test");
            statement.setInt(3, userId);
            statement.setString(4, String.valueOf(periodId));
            statement.executeUpdate();
        }

        studentMatricula = "A12345678";
        String studentSql = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(studentSql)) {
            statement.setString(1, studentMatricula);
            statement.setInt(2, 1);
            statement.setString(3, "Estudiante");
            statement.setString(4, "Apellido");
            statement.setString(5, "1234567890");
            statement.setString(6, "correo@test.com");
            statement.setString(7, "test");
            statement.setString(8, "test");
            statement.setString(9, String.valueOf(nrc));
            statement.setString(10, "100");
            statement.setDouble(11, 0.0);
            statement.executeUpdate();
        }

        String projectSql = "INSERT INTO proyecto (nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion, idDepartamento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(projectSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Proyecto Test");
            statement.setString(2, "Descripción Test");
            statement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            statement.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            statement.setInt(5, userId);
            statement.setInt(6, organizationId);
            statement.setInt(7, departmentId);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) projectId = String.valueOf(resultSet.getInt(1));
            }
        }

        String projectPresentationSql = "INSERT INTO presentacion_proyecto (idProyecto, fecha, tipo) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(projectPresentationSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, projectId);
            statement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            statement.setString(3, "Parcial");
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) presentationId = resultSet.getInt(1);
            }
        }

        String evaluationCriterionSql = "INSERT INTO criterio_de_evaluacion (nombreCriterio) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(evaluationCriterionSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Criterio Test");
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) criterionId = resultSet.getInt(1);
            }
        }
    }

    private void forceLoadData() {
        GUI_EvaluatePresentationController controller = loader.getController();
        interact(() -> controller.setPresentationIdAndTuiton(presentationId, studentMatricula));
        interact(controller::initialize);
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
    public void testCriteriaAreLoaded() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        VBox criteriaBox = lookup("#criteriaInputContainer").query();
        assertThat(criteriaBox.getChildren()).isNotEmpty();
        Label label = (Label) ((HBox) criteriaBox.getChildren().get(0)).getChildren().get(0);
        assertThat(label.getText()).contains("Criterio Test");
    }

    @Test
    public void testSaveButtonExists() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button saveButton = lookup("#saveButton").query();
        assertThat(saveButton).isNotNull();
    }

    @Test
    public void testSaveEvaluationWithValidData() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        VBox criteriaBox = lookup("#criteriaInputContainer").query();
        HBox hBox = (HBox) criteriaBox.getChildren().get(0);
        TextField scoreField = (TextField) hBox.getChildren().get(1);

        interact(() -> scoreField.setText("9.0"));

        TextArea commentArea = lookup("#commentArea").query();
        interact(() -> commentArea.setText("Buen trabajo"));

        Button saveButton = lookup("#saveButton").query();
        clickOn(saveButton);
        WaitForAsyncUtils.waitForFxEvents();

        TextField averageField = lookup("#averageGradeField").query();
        assertThat(averageField.getText()).isEqualTo("9.00");
    }

    @Test
    public void testShowErrorWhenEmptyScore() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button saveButton = lookup("#saveButton").query();
        clickOn(saveButton);
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane alert = lookup(".dialog-pane").query();
        assertThat(alert.getContentText()).contains("Hay calificaciones vacías");
        Button okButton = (Button) alert.lookupButton(ButtonType.OK);
        clickOn(okButton);
    }
}