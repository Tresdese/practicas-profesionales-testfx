package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckAcademicListController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckAcademicListControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private int testUserId;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckAcademicList.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de académicos");
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
        createTestAcademic();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM usuario");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createTestAcademic() throws SQLException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "12345");
            statement.setString(2, "Juan");
            statement.setString(3, "Pérez");
            statement.setString(4, "juanperez");
            statement.setString(5, "passTest");
            statement.setString(6, Role.ACADEMIC.getDataBaseValue());
            statement.setInt(7, 1);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    testUserId = rs.getInt(1);
                }
            }
        }
    }

    private void forceLoadData() {
        GUI_CheckAcademicListController controller = loader.getController();
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
    public void testLoadAcademicPopulatesTable() {
        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        UserDTO user = tableView.getItems().get(0);
        assertThat(user.getStaffNumber()).isEqualTo("12345");
        assertThat(user.getNames()).isEqualTo("Juan");
        assertThat(user.getSurnames()).isEqualTo("Pérez");
    }

    @Test
    public void testFailPopulateAcademicTable() throws Exception {
        clearTablesAndResetAutoIncrement();
        GUI_CheckAcademicListController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testFilterActiveAcademics() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        for (UserDTO user : tableView.getItems()) {
            assertThat(user.getStatus()).isEqualTo(1);
        }
    }

    @Test
    public void testFilterInactiveAcademics() throws Exception {
        String sql = "UPDATE usuario SET estado = 0 WHERE idUsuario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, testUserId);
            statement.executeUpdate();
        }

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Inactivos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        for (UserDTO user : tableView.getItems()) {
            assertThat(user.getStatus()).isEqualTo(0);
        }
    }

    @Test
    public void testSearchAcademicByStaffNumber() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText("12345"));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();

        assertThat(tableView.getItems().get(0).getStaffNumber()).isEqualTo("12345");
    }

    @Test
    public void testSearchAcademicByStaffNumberNotFound() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText("99999"));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testLoadAcademicsTableEmptyWhenNoAcademics() throws Exception {
        clearTablesAndResetAutoIncrement();
        GUI_CheckAcademicListController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testLoadInactiveAcademicsWhenNoInactiveAcademics() throws Exception {
        String sql = "UPDATE usuario SET estado = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }

        GUI_CheckAcademicListController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Inactivos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testLoadActiveAcademicsWhenNoActiveAcademics() throws Exception {
        String sql = "UPDATE usuario SET estado = 0";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }

        GUI_CheckAcademicListController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testRegisterAcademicButtonExists() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button registerButton = lookup("#registerAcademicButton").query();
        assertThat(registerButton).isNotNull();
    }

    @Test
    public void testDeleteButtonDisabledWhenNoAcademicSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button deleteButton = lookup("#deleteAcademicButton").query();
        assertThat(deleteButton.isDisable()).isTrue();
    }

    @Test
    public void testDeleteButtonEnabledWhenAcademicSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteAcademicButton").query();
        assertThat(deleteButton.isDisable()).isFalse();
    }

    @Test
    public void testOpenConfirmWindowWhenDelete() throws Exception {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteAcademicButton").query();
        clickOn(deleteButton);
        WaitForAsyncUtils.waitForFxEvents();


        Label confirmLabel = lookup("#confirmMessageLabel").query();
        assertThat(confirmLabel).isNotNull();
        assertThat(confirmLabel.getText()).contains("¿Está seguro de que desea eliminar al academico");

        Stage confirmStage = (Stage) confirmLabel.getScene().getWindow();
        assertThat(confirmStage.getTitle()).isEqualTo("Confirmar eliminación");
    }

    @Test
    public void testDeleteAcademic() throws Exception {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.getSelectionModel().select("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#deleteAcademicButton");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#confirmButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).isEqualTo("Academico eliminado correctamente.");
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testCancelDeleteAcademic() {

        forceLoadData();

        WaitForAsyncUtils.waitForFxEvents();

        TableView<UserDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteAcademicButton").query();
        clickOn(deleteButton);

        WaitForAsyncUtils.waitForFxEvents();

        Label confirmLabel = lookup("#confirmMessageLabel").query();
        Stage confirmStage = (Stage) confirmLabel.getScene().getWindow();
        targetWindow(confirmStage);

        Button cancelButton = lookup("#cancelButton").queryButton();
        clickOn(cancelButton);

        WaitForAsyncUtils.waitForFxEvents();

        targetWindow(window("Lista de académicos"));

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Activos"));

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(tableView.getItems()).isNotEmpty();
    }
}