package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckListOfGroupsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DTO.GroupDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckListOfGroupsControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfGroups.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de grupos");
        stage.setScene(scene);
        stage.show();
    }

    private void connectToDatabase() throws SQLException, IOException {
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
        createTestGroup();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM periodo");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createTestUser() throws SQLException {
        String sql = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, 1);
            statement.setString(2, "99999");
            statement.setString(3, "Test");
            statement.setString(4, "User");
            statement.setString(5, "testuser");
            statement.setString(6, "testpass");
            statement.setString(7, "ACADEMICO");
            statement.setInt(8, 1);
            statement.executeUpdate();
        }
    }

    private void createTestPeriod() throws SQLException {
        String sql = "INSERT INTO periodo (idPeriodo, nombre) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "1");
            statement.setString(2, "2024-1");
            statement.executeUpdate();
        }
    }

    private void createTestGroup() throws SQLException {
        createTestUser();
        createTestPeriod();
        String sql = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "123456");
            statement.setString(2, "Grupo de Prueba");
            statement.setInt(3, 1);
            statement.setString(4, "1");
            statement.executeUpdate();
        }
    }

    private void forceLoadData() {
        GUI_CheckListOfGroupsController controller = loader.getController();
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
    public void testLoadGroupsPopulatesTable() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<GroupDTO> tableView = lookup("#groupsTableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        GroupDTO group = tableView.getItems().get(0);
        assertThat(group.getNRC()).isEqualTo("123456");
        assertThat(group.getName()).isEqualTo("Grupo de Prueba");
        assertThat(group.getIdUser()).isEqualTo("1");
        assertThat(group.getIdPeriod()).isEqualTo("1");
    }

    @Test
    public void testLoadGroupsTableEmptyWhenNoGroups() throws Exception {
        clearTablesAndResetAutoIncrement();
        GUI_CheckListOfGroupsController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<GroupDTO> tableView = lookup("#groupsTableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testSearchGroupByNRC() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText("123456"));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<GroupDTO> tableView = lookup("#groupsTableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        assertThat(tableView.getItems().get(0).getNRC()).isEqualTo("123456");
    }

    @Test
    public void testSearchGroupByNRCNotFound() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText("999999"));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<GroupDTO> tableView = lookup("#groupsTableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testRegisterGroupButtonExists() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button registerButton = lookup("#registerGroupButton").query();
        assertThat(registerButton).isNotNull();
    }


}