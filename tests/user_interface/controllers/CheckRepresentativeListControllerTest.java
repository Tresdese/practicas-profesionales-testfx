package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckRepresentativeListController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.RepresentativeDAO;
import logic.DTO.RepresentativeDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckRepresentativeListControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private RepresentativeDAO representativeDAO;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckRepresentativeList.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de representantes");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        representativeDAO = new RepresentativeDAO();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        insertTestRepresentative();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearTablesAndResetAutoIncrement();
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

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE representante");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("ALTER TABLE representante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE departamento AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void insertTestRepresentative() throws SQLException, IOException {

        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO organizacion_vinculada (nombre, direccion) VALUES ('Org Test', 'Direccion Test')", Statement.RETURN_GENERATED_KEYS);
        var resultSetOrganization = statement.getGeneratedKeys();
        int organizationId = 1;
        if (resultSetOrganization.next()) {
            organizationId = resultSetOrganization.getInt(1);
        }
        statement.executeUpdate("INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES ('Dept Test', 'Desc', " + organizationId + ")", Statement.RETURN_GENERATED_KEYS);
        var resultSetDepartment = statement.getGeneratedKeys();
        int departmentId = 1;
        if (resultSetDepartment.next()) {
            departmentId = resultSetDepartment.getInt(1);
        }

        RepresentativeDTO rep = new RepresentativeDTO("1", "Nombre", "Apellido", "correo@test.com", String.valueOf(organizationId), String.valueOf(departmentId), 1);
        representativeDAO.insertRepresentative(rep);
        statement.close();
    }

    private void forceLoadData() {
        GUI_CheckRepresentativeListController controller = loader.getController();
        interact(controller::initialize);
    }

    @Test
    public void testLoadRepresentativesPopulatesTable() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        RepresentativeDTO representative = tableView.getItems().get(0);
        assertThat(representative.getNames()).isEqualTo("Nombre");
        assertThat(representative.getSurnames()).isEqualTo("Apellido");
        assertThat(representative.getEmail()).isEqualTo("correo@test.com");
    }

    @Test
    public void testLoadRepresentativesTableEmptyWhenNoRepresentatives() throws Exception {
        clearTablesAndResetAutoIncrement();
        GUI_CheckRepresentativeListController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testSearchRepresentativeByName() throws Exception {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        GUI_CheckRepresentativeListController controller = loader.getController();
        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> {
            searchField.setText("Nombre");
            searchButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).hasSize(1);
        RepresentativeDTO representative = tableView.getItems().get(0);
        assertThat(representative.getNames()).isEqualTo("Nombre");
    }

    @Test
    public void testSearchRepresentativeByNameNotFound() throws Exception {
        clearTablesAndResetAutoIncrement();

        WaitForAsyncUtils.waitForFxEvents();

        GUI_CheckRepresentativeListController controller = loader.getController();
        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> {
            searchField.setText("NonExistentName");
            searchButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();

        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testDeleteButtonEnabledWhenSelection() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        GUI_CheckRepresentativeListController controller = loader.getController();
        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        Button deleteButton = lookup("#deleteRepresentativeButton").query();

        interact(() -> {
            tableView.getSelectionModel().select(0);
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(deleteButton.isDisable()).isFalse();
    }

    @Test
    public void testDeleteButtonDisabledWhenNoSelection() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        GUI_CheckRepresentativeListController controller = loader.getController();
        Button deleteButton = lookup("#deleteRepresentativeButton").query();

        assertThat(deleteButton.isDisable()).isTrue();
    }

    @Test
    public void testDeleteRepresentative() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.getSelectionModel().select("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#deleteRepresentativeButton");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#confirmButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isEqualTo("Representante eliminado correctamente.");
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testCancelDeleteRepresentative() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.getSelectionModel().select("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#deleteRepresentativeButton");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#cancelButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isEqualTo("Eliminación cancelada.");
        assertThat(tableView.getItems()).isNotEmpty();
    }

    @Test
    public void testSelectActivesInChoiceBox() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        GUI_CheckRepresentativeListController controller = loader.getController();
        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();

        interact(() -> {
            filterChoiceBox.getSelectionModel().select("Activos");
        });
        WaitForAsyncUtils.waitForFxEvents();

        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();

        for (RepresentativeDTO representative : tableView.getItems()) {
            assertThat(representative.getStatus()).isEqualTo(1);
        }
    }

    @Test
    public void testSelectInactiveInChoiceBox() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        GUI_CheckRepresentativeListController controller = loader.getController();
        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();

        interact(() -> {
            filterChoiceBox.getSelectionModel().select("Inactivos");
        });
        WaitForAsyncUtils.waitForFxEvents();

        TableView<RepresentativeDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }
}