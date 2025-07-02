package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EvaluationPresentationDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
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
    private String studentMatricula;
    private int presentationId;
    private int periodId;
    private int nrc;
    private int departmentId;

    @BeforeAll
    void setUpAll() throws SQLException, Exception {
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
        createBaseData();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE evaluacion_presentacion");
        statement.execute("TRUNCATE TABLE presentacion_proyecto");
        statement.execute("TRUNCATE TABLE proyecto");
        statement.execute("TRUNCATE TABLE estudiante");
        statement.execute("TRUNCATE TABLE grupo");
        statement.execute("TRUNCATE TABLE periodo");
        statement.execute("TRUNCATE TABLE usuario");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
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

    private void createBaseData() throws SQLException, IOException {

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
        studentMatricula = "A12345678";

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
        String sqlQuery = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getStaffNumber());
            statement.setString(2, user.getNames());
            statement.setString(3, user.getSurnames());
            statement.setString(4, user.getUserName());
            statement.setString(5, user.getPassword());
            statement.setString(6, user.getRole().getDataBaseValue());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
    }

    @AfterAll
    void tearDownAll() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @AfterEach
    void tearDown() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    void insertEvaluationPresentation() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0,
                presentationId,
                studentMatricula,
                new java.util.Date(),
                "Comentario de prueba",
                9.5
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        assertTrue(id > 0);

        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertNotNull(found);
        assertEquals(studentMatricula, found.getTuition());
    }

    @Test
    void updateEvaluationPresentation() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 8.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);

        EvaluationPresentationDTO toUpdate = evaluationDAO.searchEvaluationPresentationById(id);
        toUpdate.setAverage(9.0);
        boolean updated = evaluationDAO.updateEvaluationPresentation(toUpdate);
        assertTrue(updated);

        EvaluationPresentationDTO updatedEvaluation = evaluationDAO.searchEvaluationPresentationById(id);
        assertEquals(9.0, updatedEvaluation.getAverage());
    }

    @Test
    void deleteEvaluationPresentation() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                -1,
                presentationId,
                studentMatricula,
                new java.util.Date(),
                "Comentario de prueba",
                8.5
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);

        boolean deleted = evaluationDAO.deleteEvaluationPresentation(id);
        assertTrue(deleted);

        EvaluationPresentationDTO deletedEval = evaluationDAO.searchEvaluationPresentationById(id);
        assertEquals(0, deletedEval.getIdEvaluation());
    }

    @Test
    void searchEvaluationPresentationById() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 8.5
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);

        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertNotNull(found);
        assertEquals(8.5, found.getAverage());
    }

    @Test
    void getAllEvaluationPresentations() throws SQLException, IOException {
        for (int i = 0; i < 3; i++) {
            EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                    0, presentationId, studentMatricula, new java.util.Date(),
                    "Comentario de prueba", 7.0 + i
            );
            evaluationDAO.insertEvaluationPresentation(evaluation);
        }
        List<EvaluationPresentationDTO> list = evaluationDAO.getAllEvaluationPresentations();
        assertEquals(3, list.size());
    }

    @Test
    void getLastInsertedId() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 10.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        assertTrue(id > 0);
    }

    @Test
    void getEvaluationPresentationsByTuition() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 9.0
        );
        evaluationDAO.insertEvaluationPresentation(evaluation);

        List<EvaluationPresentationDTO> list = evaluationDAO.getEvaluationPresentationsByTuition(studentMatricula);
        assertFalse(list.isEmpty());
        assertEquals(studentMatricula, list.get(0).getTuition());
    }

    @Test
    void insertEvaluationPresentation_withInvalidData() {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, "NOEXISTE", new java.util.Date(),
                "Comentario de prueba", -5.0
        );
        assertThrows(Exception.class, () -> evaluationDAO.insertEvaluationPresentation(evaluation));
    }

    @Test
    void updateNotExistingEvaluationPresentation() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                9999, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 8.0
        );
        boolean updated = evaluationDAO.updateEvaluationPresentation(evaluation);
        assertFalse(updated);
    }

    @Test
    void deleteNotExistingEvaluationPresentation() throws SQLException, IOException {
        boolean deleted = evaluationDAO.deleteEvaluationPresentation(9999);
        assertFalse(deleted);
    }

    @Test
    void getAllEvaluationPresentationsWithNoData() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        List<EvaluationPresentationDTO> list = evaluationDAO.getAllEvaluationPresentations();
        assertTrue(list.isEmpty());
    }

    @Test
    void getNoResultsEvaluationPresentationsByTuition() throws SQLException, IOException {
        List<EvaluationPresentationDTO> list = evaluationDAO.getEvaluationPresentationsByTuition("NOEXISTE");
        assertTrue(list.isEmpty());
    }

    @Test
    void insertNullDateEvaluationPresentation() {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, null,
                "Comentario de prueba", 8.0
        );
        assertThrows(Exception.class, () -> evaluationDAO.insertEvaluationPresentation(evaluation));
    }

    @Test
    void searchNotExistingEvaluationPresentationById() throws SQLException, IOException {
        int nonExistentId = 99999;
        EvaluationPresentationDTO result = evaluationDAO.searchEvaluationPresentationById(nonExistentId);
        assertEquals(0, result.getIdEvaluation());
    }

    @Test
    void insertEvaluationPresentationWithUpperLimitAverage() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 10.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        assertTrue(id > 0);
        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertEquals(10.0, found.getAverage());
    }

    @Test
    void insertEvaluationPresentationWithLowerLimitAverage() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 0.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        assertTrue(id > 0);
        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertEquals(0.0, found.getAverage());
    }

    @Test
    void getEvaluationPresentationsByTuitionMultipleRecords() throws SQLException, IOException {
        for (int i = 0; i < 3; i++) {
            EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                    0, presentationId, studentMatricula, new java.util.Date(),
                    "Comentario de prueba", 7.0 + i
            );
            evaluationDAO.insertEvaluationPresentation(evaluation);
        }
        List<EvaluationPresentationDTO> list = evaluationDAO.getEvaluationPresentationsByTuition(studentMatricula);
        assertEquals(3, list.size());
    }

    @Test
    void deleteEvaluationPresentationWithMultipleRecords() throws SQLException, IOException {
        int[] ids = new int[3];
        for (int i = 0; i < 3; i++) {
            EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                    0,
                    presentationId,
                    studentMatricula,
                    new java.util.Date(),
                    "Comentario " + i,
                    8.0 + i
            );
            ids[i] = evaluationDAO.insertEvaluationPresentation(evaluation);
        }

        boolean deleted = evaluationDAO.deleteEvaluationPresentation(ids[1]);
        assertTrue(deleted);

        EvaluationPresentationDTO deletedEval = evaluationDAO.searchEvaluationPresentationById(ids[1]);
        assertEquals(0, deletedEval.getIdEvaluation());
    }

    @Test
    void insertNullTuitionEvaluationPresentation() {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, null, new java.util.Date(),
                "Comentario de prueba", 8.0
        );
        assertThrows(Exception.class, () -> evaluationDAO.insertEvaluationPresentation(evaluation));
    }

    @Test
    void insertInvalidPresentationIdEvaluationPresentation() {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, 9999, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 8.0
        );
        assertThrows(Exception.class, () -> evaluationDAO.insertEvaluationPresentation(evaluation));
    }

    @Test
    void updateNullDateEvaluationPresentation() throws SQLException, IOException {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 8.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        EvaluationPresentationDTO toUpdate = evaluationDAO.searchEvaluationPresentationById(id);
        toUpdate.setDate(null);
        assertThrows(Exception.class, () -> evaluationDAO.updateEvaluationPresentation(toUpdate));
    }
}