package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckListOfParticipantsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DTO.StudentProjectViewDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckListOfParticipantsControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;
    private int testPresentationId;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfParticipants.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de participantes");
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
        createTestPresentation();
        createTestParticipant();
    }

    private void clearTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM estudiante_proyecto");
        statement.execute("DELETE FROM presentacion_proyecto");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM proyecto");
        statement.execute("DELETE FROM presentacion");
        statement.execute("ALTER TABLE estudiante_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE presentacion AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createTestPresentation() throws SQLException {
        String sqlProject = "INSERT INTO proyecto (idProyecto, nombre) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlProject)) {
            statement.setInt(1, 1);
            statement.setString(2, "Proyecto Prueba");
            statement.executeUpdate();
        }
        String sqlPresentation = "INSERT INTO presentacion (idPresentacion, idProyecto) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlPresentation, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, 1);
            statement.setInt(2, 1);
            statement.executeUpdate();
            testPresentationId = 1;
        }
    }

    private void createTestParticipant() throws SQLException {
        String sqlStudent = "INSERT INTO estudiante (matricula, nombre) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlStudent)) {
            statement.setString(1, "A12345678");
            statement.setString(2, "Estudiante Prueba");
            statement.executeUpdate();
        }
        String sqlStudentProject = "INSERT INTO estudiante_proyecto (matricula, idProyecto, idPresentacion) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlStudentProject)) {
            statement.setString(1, "A12345678");
            statement.setInt(2, 1);
            statement.setInt(3, testPresentationId);
            statement.executeUpdate();
        }
    }

    private void forceLoadData() {
        GUI_CheckListOfParticipantsController controller = loader.getController();
        interact(() -> {
            controller.setPresentationId(testPresentationId);
        });
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
    public void testLoadParticipantsPopulatesTable() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentProjectViewDTO> tableView = lookup("#participantsTableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        StudentProjectViewDTO participant = tableView.getItems().get(0);
        assertThat(participant.getStudentMatricula()).isEqualTo("A12345678");
        assertThat(participant.getStudentName()).isEqualTo("Estudiante Prueba");
        assertThat(participant.getProjectName()).isEqualTo("Proyecto Prueba");
    }

    @Test
    public void testLoadParticipantsTableEmptyWhenNoParticipants() throws Exception {
        clearTables();
        GUI_CheckListOfParticipantsController controller = loader.getController();
        interact(() -> controller.setPresentationId(testPresentationId));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<StudentProjectViewDTO> tableView = lookup("#participantsTableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testParticipantCountsLabelShowsTotal() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Label countsLabel = lookup("#participantCountsLabel").query();
        assertThat(countsLabel.getText()).contains("Totales: 1");
    }
}