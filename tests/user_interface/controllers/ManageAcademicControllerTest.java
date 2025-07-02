package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_ManageAcademicController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ManageAcademicControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;
    private UserDTO testAcademic;
    private String testUserId = "12345";

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageAcademic.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Gestión de Académico");
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
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTable();
        createTestAcademic();
        interact(this::setAcademicDataInController);
    }

    private void clearTable() throws SQLException {
        connection.prepareStatement("DELETE FROM grupo").executeUpdate();
        connection.prepareStatement("DELETE FROM usuario").executeUpdate();
    }

    private void createTestAcademic() throws SQLException, IOException {
        testAcademic = new UserDTO(
                testUserId,
                1,
                "12345",
                "Luis",
                "Martínez",
                "luismtz",
                "password",
                Role.ACADEMIC
        );
        UserDAO userDAO = new UserDAO();
        userDAO.insertUser(testAcademic);
    }

    private void setAcademicDataInController() {
        GUI_ManageAcademicController controller = loader.getController();
        controller.setAcademicData(testAcademic);
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
    public void testEditAndSaveAcademic() throws Exception {
        WaitForAsyncUtils.waitForFxEvents();
        UserDAO userDAO = new UserDAO();

        TextField namesField = lookup("#namesField").query();
        TextField surnamesField = lookup("#surnamesField").query();
        ChoiceBox<Role> roleBox = lookup("#roleChoiceBox").query();
        Button saveButton = lookup(".button")
                .queryAll()
                .stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Guardar"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        interact(() -> {
            namesField.setText("Luis Modificado");
            surnamesField.setText("Martínez Mod");
            roleBox.setValue(Role.EVALUATOR_ACADEMIC);
        });

        clickOn(saveButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();

        UserDTO updated = userDAO.searchUserByStaffNumber(testUserId);

        assertThat(statusLabel.getText()).contains("¡Académico actualizado exitosamente!");
        assertThat(updated.getNames()).isEqualTo("Luis Modificado");
        assertThat(updated.getSurnames()).isEqualTo("Martínez Mod");
        assertThat(updated.getRole()).isEqualTo(Role.EVALUATOR_ACADEMIC);
    }

    @Test
    public void testSaveButtonDisabledInitially() {
        WaitForAsyncUtils.waitForFxEvents();
        Button saveButton = lookup(".button")
                .queryAll()
                .stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Guardar"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        assertThat(saveButton.isDisable()).isTrue();
    }

}