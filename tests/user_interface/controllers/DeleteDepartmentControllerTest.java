package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_DeleteDepartmentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.DepartmentDAO;
import logic.DTO.DepartmentDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeleteDepartmentControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private int testOrganizationId;
    private int testDepartmentId;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_DeleteDepartment.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Eliminar Departamento");
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
        createTestOrganization();
        createTestDepartment();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM departamento");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.close();
    }

    private void createTestOrganization() throws SQLException {
        String sql = "INSERT INTO organizacion_vinculada (nombre, direccion, estado) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, "OrgTest");
        statement.setString(2, "Calle Falsa 123");
        statement.setInt(3, 1);
        statement.executeUpdate();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
            testOrganizationId = rs.getInt(1);
        }
        statement.close();
    }

    private void createTestDepartment() throws SQLException {
        String sql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion, estado) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, "DeptTest");
        statement.setString(2, "Departamento de prueba");
        statement.setInt(3, testOrganizationId);
        statement.setInt(4, 1);
        statement.executeUpdate();
        ResultSet rs = statement.getGeneratedKeys();
        if (rs.next()) {
            testDepartmentId = rs.getInt(1);
        }
        statement.close();
    }

    private void forceLoadData() {
        GUI_DeleteDepartmentController controller = loader.getController();
        interact(() -> controller.setOrganizationId(String.valueOf(testOrganizationId)));
    }

    @AfterAll
    void tearDownAll() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    public void testLoadDepartmentsPopulatesChoiceBox() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<DepartmentDTO> departmentsChoiceBox = lookup("#departmentsChoiceBox").query();
        assertThat(departmentsChoiceBox.getItems()).isNotEmpty();
    }

    @Test
    public void testDeleteButtonDisabledWhenNoDepartmentSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#departmentsChoiceBox");
        type(javafx.scene.input.KeyCode.ESCAPE);

        clickOn("#deleteButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("Seleccione un departamento");
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<DepartmentDTO> departmentsChoiceBox = lookup("#departmentsChoiceBox").query();
        interact(() -> departmentsChoiceBox.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteButton").query();
        clickOn(deleteButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label confirmLabel = lookup("#confirmMessageLabel").query();
        Stage confirmStage = (Stage) confirmLabel.getScene().getWindow();
        targetWindow(confirmStage);

        Button confirmButton = lookup("#confirmButton").queryButton();
        clickOn(confirmButton);
        WaitForAsyncUtils.waitForFxEvents();

        targetWindow(window("Eliminar Departamento"));

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(departmentsChoiceBox.getItems()).isEmpty();
        assertThat(statusLabel.getText()).contains("No hay departamentos activos.");
    }

    @Test
    public void testStatusLabelShowsNoDepartmentsWhenEmpty() throws Exception {
        clearTablesAndResetAutoIncrement();

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("No hay departamentos activos");
    }

    @Test
    public void testCancelDeleteDepartment() throws Exception {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<DepartmentDTO> departmentsChoiceBox = lookup("#departmentsChoiceBox").query();
        interact(() -> departmentsChoiceBox.getSelectionModel().select(0));

        clickOn("#deleteButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label confirmLabel = lookup("#confirmMessageLabel").query();
        Stage confirmStage = (Stage) confirmLabel.getScene().getWindow();
        targetWindow(confirmStage);

        clickOn("#cancelButton");
        WaitForAsyncUtils.waitForFxEvents();

        targetWindow(window("Eliminar Departamento"));
        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("Eliminación cancelada.");
    }
}