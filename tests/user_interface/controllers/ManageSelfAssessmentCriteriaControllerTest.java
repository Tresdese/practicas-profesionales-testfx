package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_ManageSelfAssessmentCriteriaController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.SelfAssessmentCriteriaDAO;
import logic.DTO.SelfAssessmentCriteriaDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ManageSelfAssessmentCriteriaControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;
    private SelfAssessmentCriteriaDAO criteriaDAO;
    private SelfAssessmentCriteriaDTO testCriteria;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageSelfAssessmentCriteria.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Gestión de Criterios de Autoevaluación");
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
        criteriaDAO = new SelfAssessmentCriteriaDAO();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTable();
        createTestCriteria();
        interact(this::setCriteriaInController);
    }

    private void clearTable() throws SQLException {
        connection.prepareStatement("DELETE FROM criterio_de_autoevaluacion").executeUpdate();
    }

    private void createTestCriteria() throws SQLException, IOException {
        testCriteria = new SelfAssessmentCriteriaDTO("1", "Participación");
        criteriaDAO.insertSelfAssessmentCriteria(testCriteria);
    }

    private void setCriteriaInController() {
        GUI_ManageSelfAssessmentCriteriaController controller = loader.getController();
    }

    private void forceLoadData() {
        GUI_ManageSelfAssessmentCriteriaController controller = loader.getController();
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
        clearTable();
    }

    @Test
    public void testRegisterCriteria() {
        WaitForAsyncUtils.waitForFxEvents();
        TextField idField = lookup("#idCriteriaField").query();
        TextField nameField = lookup("#nameCriteriaField").query();
        Button registerButton = lookup("#registerButton").query();

        interact(() -> {
            idField.setText("2");
            nameField.setText("Trabajo en equipo");
        });

        clickOn(registerButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<SelfAssessmentCriteriaDTO> table = lookup("#criteriaTable").query();
        assertThat(table.getItems()).anyMatch(c -> Integer.parseInt(c.getIdCriteria()) == 2 && "Trabajo en equipo".equals(c.getNameCriteria()));

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("Criterio registrado correctamente");
    }

    @Test
    public void testRegisterCriteriaWithEmptyFields() {
        WaitForAsyncUtils.waitForFxEvents();
        TextField idField = lookup("#idCriteriaField").query();
        TextField nameField = lookup("#nameCriteriaField").query();
        Button registerButton = lookup("#registerButton").query();

        interact(() -> {
            idField.setText("");
            nameField.setText("");
        });

        clickOn(registerButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("Todos los campos son obligatorios.");
    }

    @Test
    public void testUpdateCriteria() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();
        TableView<SelfAssessmentCriteriaDTO> table = lookup("#criteriaTable").query();
        interact(() -> table.getSelectionModel().selectFirst());

        TextField nameField = lookup("#nameCriteriaField").query();
        Button updateButton = lookup("#updateButton").query();

        interact(() -> nameField.setText("Participación activa"));
        clickOn(updateButton);
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(table.getItems()).anyMatch(c -> Integer.parseInt(c.getIdCriteria()) == 1 && "Participación activa".equals(c.getNameCriteria()));

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("Criterio actualizado correctamente");
    }

    @Test
    public void testDeleteCriteria() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();
        TableView<SelfAssessmentCriteriaDTO> table = lookup("#criteriaTable").query();
        interact(() -> table.getSelectionModel().selectFirst());

        Button deleteButton = lookup("#deleteButton").query();
        clickOn(deleteButton);
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(table.getItems()).noneMatch(c -> Integer.parseInt(c.getIdCriteria()) == 1);

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("Criterio eliminado correctamente");
    }

    @Test
    public void testTablePopulatedInitially() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();
        TableView<SelfAssessmentCriteriaDTO> table = lookup("#criteriaTable").query();
        assertThat(table.getItems()).anyMatch(c -> Integer.parseInt(c.getIdCriteria()) == 1 && "Participación".equals(c.getNameCriteria()));
    }
}