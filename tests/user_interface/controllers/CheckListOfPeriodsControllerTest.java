package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckListOfPeriodsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DTO.PeriodDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;
import java.sql.Timestamp;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckListOfPeriodsControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfPeriods.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de periodos");
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
    }

    private void clearTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM periodo");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
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

    private void forceLoadData() {
        GUI_CheckListOfPeriodsController controller = loader.getController();
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
        clearTables();
    }

    @Test
    public void testLoadPeriodsPopulatesTable() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<PeriodDTO> tableView = lookup("#periodsTableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        PeriodDTO period = tableView.getItems().get(0);
        assertThat(period.getIdPeriod()).isEqualTo("1");
        assertThat(period.getName()).isEqualTo("2024-1");
    }

    @Test
    public void testLoadPeriodsTableEmptyWhenNoPeriods() throws Exception {
        clearTables();
        GUI_CheckListOfPeriodsController controller = loader.getController();
        interact(controller::initialize);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<PeriodDTO> tableView = lookup("#periodsTableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testSearchPeriodById() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText("1"));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<PeriodDTO> tableView = lookup("#periodsTableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        assertThat(tableView.getItems().get(0).getIdPeriod()).isEqualTo("1");
    }

    @Test
    public void testSearchPeriodByIdNotFound() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TextField searchField = lookup("#searchField").query();
        Button searchButton = lookup("#searchButton").query();

        interact(() -> searchField.setText("99999"));
        clickOn(searchButton);
        WaitForAsyncUtils.waitForFxEvents();

        TableView<PeriodDTO> tableView = lookup("#periodsTableView").query();
        assertThat(tableView.getItems()).isEmpty();
    }

    @Test
    public void testRegisterPeriodButtonExists() {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        Button registerButton = lookup("#registerPeriodButton").query();
        assertThat(registerButton).isNotNull();
    }
}