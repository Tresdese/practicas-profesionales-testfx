package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_ManageRepresentativeController;
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

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ManageRepresentativeControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;
    private RepresentativeDTO testRepresentative;
    private String testRepId = "1001";
    private int testOrgId;
    private int testDeptId;

    @Override
    public void start(Stage stage) throws Exception {
        connectToDatabase();
        clearTables();
        createTestOrganizationAndDepartment();

        loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageRepresentative.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Gestión de Representante");
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
        createTestOrganizationAndDepartment();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTables();
        createTestOrganizationAndDepartment();
        createTestRepresentative();
        interact(this::setRepresentativeDataInController);
    }

    private void clearTables() throws SQLException {
        connection.prepareStatement("DELETE FROM representante").executeUpdate();
        connection.prepareStatement("DELETE FROM departamento").executeUpdate();
        connection.prepareStatement("DELETE FROM organizacion_vinculada").executeUpdate();
    }

    private void createTestOrganizationAndDepartment() throws SQLException {

        String orgSql = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES ('OrgTest', 'DirTest')";
        try (var stmt = connection.prepareStatement(orgSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                testOrgId = rs.getInt(1);
            }
        }

        String deptSql = "INSERT INTO departamento (nombre, descripcion, idOrganizacion) VALUES ('DeptTest', 'Desc', ?)";
        try (var stmt = connection.prepareStatement(deptSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, testOrgId);
            stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                testDeptId = rs.getInt(1);
            }
        }
    }

    private void createTestRepresentative() throws SQLException {
        String sql = "INSERT INTO representante (idRepresentante, nombres, apellidos, correo, idOrganizacion, idDepartamento, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, testRepId);
            stmt.setString(2, "Juan");
            stmt.setString(3, "Pérez");
            stmt.setString(4, "juanperez@example.com");
            stmt.setString(5, String.valueOf(testOrgId));
            stmt.setString(6, String.valueOf(testDeptId));
            stmt.setInt(7, 1);
            stmt.executeUpdate();
        }
        testRepresentative = new RepresentativeDTO(
                testRepId,
                "Juan",
                "Pérez",
                "juanperez@example.com",
                String.valueOf(testOrgId),
                String.valueOf(testDeptId),
                1
        );
    }

    private void setRepresentativeDataInController() {
        GUI_ManageRepresentativeController controller = loader.getController();
        controller.setRepresentativeData(testRepresentative);
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
    public void testRepresentativeDataLoadedCorrectly() {
        WaitForAsyncUtils.waitForFxEvents();
        TextField namesField = lookup("#namesField").query();
        TextField surnamesField = lookup("#surnamesField").query();
        TextField emailField = lookup("#emailField").query();
        ChoiceBox<String> orgBox = lookup("#organizationChoiceBox").query();
        ChoiceBox<String> deptBox = lookup("#departmentChoiceBox").query();

        assertThat(namesField.getText()).isEqualTo(testRepresentative.getNames());
        assertThat(surnamesField.getText()).isEqualTo(testRepresentative.getSurnames());
        assertThat(emailField.getText()).isEqualTo(testRepresentative.getEmail());
        assertThat(orgBox.getValue()).isEqualTo("OrgTest");
        assertThat(deptBox.getValue()).isEqualTo("DeptTest");
    }

    @Test
    public void testPopulateOrganizationChoiceBox() {
        WaitForAsyncUtils.waitForFxEvents();
        ChoiceBox<String> orgBox = lookup("#organizationChoiceBox").query();
        assertThat(orgBox.getItems()).contains("OrgTest");
    }

    @Test
    public void testPopulateDepartmentChoiceBox() {
        WaitForAsyncUtils.waitForFxEvents();
        ChoiceBox<String> deptBox = lookup("#departmentChoiceBox").query();
        assertThat(deptBox.getItems()).contains("DeptTest");
    }

    @Test
    public void testEditAndSaveRepresentative() throws Exception {
        WaitForAsyncUtils.waitForFxEvents();
        RepresentativeDAO repDAO = new RepresentativeDAO();

        TextField namesField = lookup("#namesField").query();
        TextField surnamesField = lookup("#surnamesField").query();
        TextField emailField = lookup("#emailField").query();
        ChoiceBox<String> orgBox = lookup("#organizationChoiceBox").query();
        ChoiceBox<String> deptBox = lookup("#departmentChoiceBox").query();
        Button saveButton = lookup(".button")
                .queryAll()
                .stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Guardar"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        interact(() -> {
            namesField.setText("Juan Modificado");
            surnamesField.setText("Pérez Mod");
            emailField.setText("modificado@example.com");
            orgBox.setValue("OrgTest");
            deptBox.setValue("DeptTest");
        });

        clickOn(saveButton);
        WaitForAsyncUtils.waitForFxEvents();

        Label statusLabel = lookup("#statusLabel").query();

        RepresentativeDTO updated = repDAO.searchRepresentativeById(testRepId);

        assertThat(statusLabel.getText()).contains("¡Representante actualizado exitosamente!");
        assertThat(updated.getNames()).isEqualTo("Juan Modificado");
        assertThat(updated.getSurnames()).isEqualTo("Pérez Mod");
        assertThat(updated.getEmail()).isEqualTo("modificado@example.com");
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

    @Test
    public void testSaveButtonEnabledWhenDataChanged() {
        WaitForAsyncUtils.waitForFxEvents();
        TextField namesField = lookup("#namesField").query();
        TextField surnamesField = lookup("#surnamesField").query();
        TextField emailField = lookup("#emailField").query();

        Button saveButton = lookup(".button")
                .queryAll()
                .stream()
                .filter(node -> node instanceof Button && ((Button) node).getText().contains("Guardar"))
                .map(node -> (Button) node)
                .findFirst()
                .orElseThrow();

        assertThat(saveButton.isDisable()).isTrue();

        interact(() -> {
            namesField.setText("Nuevo Nombre");
            surnamesField.setText("Nuevo Apellido");
            emailField.setText("nuevo@email.com");
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(saveButton.isDisable()).isFalse();
    }

}