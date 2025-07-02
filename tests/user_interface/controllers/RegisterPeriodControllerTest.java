package user_interface.controllers;

import data_access.ConnectionDataBase;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import logic.DAO.PeriodDAO;
import logic.DTO.PeriodDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterPeriodControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private PeriodDAO periodDAO;
    private FXMLLoader loader;

    private final String baseId = "1000";
    private final String baseName = "Periodo Base";
    private final Timestamp baseStart = Timestamp.valueOf("2025-01-01 00:00:00");
    private final Timestamp baseEnd = Timestamp.valueOf("2025-06-30 23:59:59");

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterPeriod.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Registrar periodo");
        stage.setScene(scene);
        stage.show();
    }

    void connectToDatabase() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
        }
    }

    void setServices() {
        periodDAO = new PeriodDAO();
    }

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectToDatabase();
        setServices();
        clearTable();
        periodDAO.insertPeriod(new PeriodDTO(baseId, baseName, baseStart, baseEnd));
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTable();
        periodDAO.insertPeriod(new PeriodDTO(baseId, baseName, baseStart, baseEnd));
    }

    private void clearTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM periodo")) {
            statement.executeUpdate();
        }
        try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE periodo AUTO_INCREMENT = 1")) {
            statement.executeUpdate();
        } catch (SQLException ignored) { }
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        clearTable();
        if (connection != null) connection.close();
        if (connectionDB != null) connectionDB.close();
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearTable();
    }

    @Test
    public void testSuccessPeriodRegister() {
        clickOn("#periodLabel").write("2000");
        clickOn("#nameField").write("Periodo Test");
        interact(() -> {
            ((DatePicker) lookup("#startDateLabel").query()).setValue(LocalDate.of(2026, 1, 1));
            ((DatePicker) lookup("#endDateField").query()).setValue(LocalDate.of(2026, 6, 30));
        });

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("¡Periodo registrado exitosamente!");
    }

    @Test
    public void testFailureRegisterWithInvalidId() {
        clickOn("#periodLabel").write("abc123");
        clickOn("#nameField").write("Periodo Invalido");

        interact(() -> {
            ((DatePicker) lookup("#startDateLabel").query()).setValue(LocalDate.of(2025, 1, 1));
            ((DatePicker) lookup("#endDateField").query()).setValue(LocalDate.of(2025, 6, 30));
        });

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("El ID del periodo solo puede contener números.");
    }

    @Test
    public void testFailureRegisterWithoutFields() {
        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Todos los campos deben estar llenos.");
    }

    @Test
    public void testFailureRegisterWithDuplicateId() {
        clickOn("#periodLabel").write(baseId);
        clickOn("#nameField").write("Periodo Duplicado");

        interact(() -> {
            ((DatePicker) lookup("#startDateLabel").query()).setValue(LocalDate.of(2025, 1, 1));
            ((DatePicker) lookup("#endDateField").query()).setValue(LocalDate.of(2025, 6, 30));
        });

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("El ID del periodo ya existe.");
    }
}