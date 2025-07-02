package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckListLinkedOrganizationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckListLinkedOrganizationControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private int testOrganizationId;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListLinkedOrganization.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de Organizaciones");
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
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
         Statement statement = connection.createStatement();
         statement.execute("DELETE FROM organizacion_vinculada");
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

    private void forceLoadData() {
        GUI_CheckListLinkedOrganizationController controller = loader.getController();
        interact(controller::initialize);
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
    public void testLoadOrganizationsPopulatesTable() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
    }

    @Test
    public void testFilterActiveOrganizations() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        for (LinkedOrganizationDTO org : tableView.getItems()) {
            assertThat(org.getStatus()).isEqualTo(1);
        }
    }

    @Test
    public void testFilterInactiveOrganizations() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Inactivos"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testSearchOrganizationByName() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        interact(() -> searchField.setText("OrgTest"));
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).hasSize(1);
        assertThat(tableView.getItems().get(0).getName()).isEqualTo("OrgTest");
    }

    @Test
    public void testSearchOrganizationByNameNotFound() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText("NonExistentOrg"));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testRegisterOrganizationButtonExists() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button registerButton = lookup("#registerOrganizationButton").query();
        assertThat(registerButton).isNotNull();
    }

    @Test
    public void testDeleteButtonDisabledWhenNoOrganizationSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button deleteButton = lookup("#deleteOrganizationButton").query();
        assertThat(deleteButton.isDisable()).isTrue();
    }

    @Test
    public void testOpenConfirmationDialogOnDeleteButtonClick() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteOrganizationButton").query();
        clickOn(deleteButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label confirmLabel = lookup("#confirmMessageLabel").query();
        assertThat(confirmLabel).isNotNull();
    }

    @Test
    public void testDeleteButtonEnabledWhenOrganizationSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteOrganizationButton").query();
        assertThat(deleteButton.isDisable()).isFalse();
    }

    @Test
    public void testDeleteOrganization() throws Exception {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteOrganizationButton").query();
        clickOn(deleteButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label confirmLabel = lookup("#confirmMessageLabel").query();
        Stage confirmStage = (Stage) confirmLabel.getScene().getWindow();
        targetWindow(confirmStage);

        Button confirmButton = lookup("#confirmButton").queryButton();
        clickOn(confirmButton);
        WaitForAsyncUtils.waitForFxEvents();

        targetWindow(window("Lista de Organizaciones"));

        ChoiceBox<String> filterChoiceBox = lookup("#filterChoiceBox").query();
        interact(() -> filterChoiceBox.setValue("Activos"));
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(tableView.getItems()).isEmpty();
        assertThat(statusLabel.getText()).contains("Organizacion eliminada correctamente.");
    }

    @Test
    public void testCancelDeleteOrganization() throws Exception {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteButton = lookup("#deleteOrganizationButton").query();
        clickOn(deleteButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label confirmLabel = lookup("#confirmMessageLabel").query();
        Stage confirmStage = (Stage) confirmLabel.getScene().getWindow();
        targetWindow(confirmStage);

        Button cancelButton = lookup("#cancelButton").queryButton();
        clickOn(cancelButton);
        WaitForAsyncUtils.waitForFxEvents();

        targetWindow(window("Lista de Organizaciones"));

        TableView<LinkedOrganizationDTO> updatedTableView = lookup("#tableView").query();
        Label statusLabel = lookup("#statusLabel").query();

        assertThat(updatedTableView.getItems()).isNotEmpty();
        assertThat(statusLabel.getText()).contains("Eliminación cancelada.");
    }

    @Test
    public void testDeleteDepartmentButtonDisabledWhenNoOrganizationSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button deleteDepartmentButton = lookup("#deleteDepartmentButton").query();
        assertThat(deleteDepartmentButton.isDisable()).isTrue();
    }

    @Test
    public void testDeleteDepartmentButtonEnabledWhenOrganizationSelected() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));

        Button deleteDepartmentButton = lookup("#deleteDepartmentButton").query();

        assertThat(deleteDepartmentButton.isDisable()).isFalse();
    }

    @Test
    public void testManagementButtonAppearsOnRowSelection() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<LinkedOrganizationDTO> tableView = lookup("#tableView").query();
        interact(() -> tableView.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        Button manageButton = lookup(".button").lookup((Node node) ->
                node instanceof Button && "Gestionar Organización".equals(((Button) node).getText())
        ).queryButton();
        assertThat(manageButton).isNotNull();
    }
}