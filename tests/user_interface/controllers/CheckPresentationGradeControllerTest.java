package user_interface.controllers;

import data_access.ConnectionDataBase;
import gui.GUI_CheckPresentationGradeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckPresentationGradeControllerTest extends ApplicationTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private FXMLLoader loader;


    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private LinkedOrganizationDAO organizationDAO;
    private ProjectDAO projectDAO;
    private StudentDAO studentDAO;
    private ProjectPresentationDAO presentationDAO;
    private EvaluationPresentationDAO evaluationDAO;
    private PeriodDAO periodDAO;
    private GroupDAO groupDAO;


    private int userId;
    private int organizationId;
    private String projectId;
    private String studentTuition;
    private int presentationId;
    private int periodId;
    private int nrc;
    private int departmentId;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckPresentationGrade.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Lista de calificaciones de presentación");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        organizationDAO = new LinkedOrganizationDAO();
        projectDAO = new ProjectDAO();
        studentDAO = new StudentDAO();
        presentationDAO = new ProjectPresentationDAO();
        evaluationDAO = new EvaluationPresentationDAO();
        periodDAO = new PeriodDAO();
        groupDAO = new GroupDAO();

        clearTablesAndResetAutoIncrement();
        createDataBaseResources();
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createDataBaseResources();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("DELETE FROM evaluacion_presentacion");
        statement.execute("DELETE FROM presentacion_proyecto");
        statement.execute("DELETE FROM proyecto");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM periodo");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM departamento");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("ALTER TABLE evaluacion_presentacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createDataBaseResources() throws SQLException, IOException {

        LinkedOrganizationDTO org = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test", 1);
        organizationId = Integer.parseInt(organizationDAO.insertLinkedOrganizationAndGetId(org));

        UserDTO user = new UserDTO(null, 1, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMIC);
        userId = insertUserAndGetId(user);

        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Descripción test", organizationId, 1);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departments.get(0).getDepartmentId();

        periodId = 20241;
        PeriodDTO period = new PeriodDTO(String.valueOf(periodId), "2024-1", new java.sql.Timestamp(System.currentTimeMillis()), new java.sql.Timestamp(System.currentTimeMillis() + 1000000));
        periodDAO.insertPeriod(period);

        nrc = 12345;
        GroupDTO group = new GroupDTO(String.valueOf(nrc), "Grupo Test", String.valueOf(userId), String.valueOf(periodId));
        groupDAO.insertGroup(group);

        StudentDTO student = new StudentDTO(
                "A12345678",
                1,
                "Estudiante",
                "Apellido",
                "1234567890",
                "correo@test.com",
                "test",
                "test",
                String.valueOf(nrc),
                "100",
                0.0
        );
        studentDAO.insertStudent(student);
        studentTuition = "A12345678";

        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Test",
                "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId,
                departmentId
        );
        projectDAO.insertProject(project);
        projectId = projectDAO.getAllProjects().get(0).getIdProject();

        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Type.Partial
        );
        presentationDAO.insertProjectPresentation(presentation);
        presentationId = presentationDAO.getAllProjectPresentations().get(0).getIdPresentation();
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getStaffNumber());
            statement.setString(2, user.getNames());
            statement.setString(3, user.getSurnames());
            statement.setString(4, user.getUserName());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getRole().getDataBaseValue());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    private void forceLoadData() throws IOException, SQLException {
        GUI_CheckPresentationGradeController controller = loader.getController();
        StudentDTO student = studentDAO.searchStudentByTuition(studentTuition);
        interact(() -> controller.setStudent(student));
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
    public void testLoadPresentationGradesPopulatesTable() throws Exception {

        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0,
                presentationId,
                studentTuition,
                new Date(),
                "Comentario de prueba",
                9.5
        );
        evaluationDAO.insertEvaluationPresentation(evaluation);

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<EvaluationPresentationDTO> tableView = lookup("#presentationGradeTableView").query();
        assertThat(tableView.getItems()).isNotEmpty();
        EvaluationPresentationDTO eval = tableView.getItems().get(0);
        assertThat(eval.getTuition()).isEqualTo(studentTuition);
        assertThat(eval.getAverage()).isEqualTo(9.5);
    }

    @Test
    public void testLoadPresentationGradesTableEmptyWhenNoEvaluations() throws SQLException, IOException {
        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<EvaluationPresentationDTO> tableView = lookup("#presentationGradeTableView").query();
        assertThat(tableView.getItems()).isEmpty();

        Label statusLabel = lookup("#statusLabel").query();
        assertThat(statusLabel.getText()).contains("No tienes evaluaciones de presentación registradas.");
    }

    @Test
    public void testDetailsButtonIsVisible() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0,
                presentationId,
                studentTuition,
                new Date(),
                "Comentario de prueba",
                9.5
        );
        evaluationDAO.insertEvaluationPresentation(evaluation);

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<EvaluationPresentationDTO> tableView = lookup("#presentationGradeTableView").query();
        interact(() -> tableView.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        Button detailsButton = lookup(".button").queryAll().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .filter(button -> "Ver Detalles".equals(button.getText()))
                .findFirst()
                .orElse(null);

        assertThat(detailsButton).isNotNull();
        assertThat(detailsButton.isVisible()).isTrue();
    }

    @Test
    public void testDetailsButtonOpensDetailsWindow() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0,
                presentationId,
                studentTuition,
                new Date(),
                "Comentario de prueba",
                9.5
        );
        evaluationDAO.insertEvaluationPresentation(evaluation);

        forceLoadData();
        WaitForAsyncUtils.waitForFxEvents();

        TableView<EvaluationPresentationDTO> tableView = lookup("#presentationGradeTableView").query();
        interact(() -> tableView.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        Button detailsButton = lookup(".button").queryAll().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .filter(button -> "Ver Detalles".equals(button.getText()))
                .findFirst()
                .orElse(null);

        assertThat(detailsButton).isNotNull();
        clickOn(detailsButton);
        WaitForAsyncUtils.waitForFxEvents();

        Stage detailsStage = (Stage) detailsButton.getScene().getWindow();
        assertThat(detailsStage.isShowing()).isTrue();
    }
}