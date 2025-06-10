package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EvaluationPresentationDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
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

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        userDAO = new UserDAO();
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
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseData();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE evaluacion_presentacion");
        stmt.execute("TRUNCATE TABLE presentacion_proyecto");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("ALTER TABLE evaluacion_presentacion AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void createBaseData() throws SQLException {
        // Organization
        LinkedOrganizationDTO org = new LinkedOrganizationDTO(null, "Org Test", "Dirección Test");
        organizationId = Integer.parseInt(organizationDAO.insertLinkedOrganizationAndGetId(org));

        // User
        UserDTO user = new UserDTO(null, "12345", "Nombre", "Apellido", "usuarioTest", "passTest", Role.ACADEMICO);
        userId = insertUserAndGetId(user);

        // Period
        periodId = 20241;
        PeriodDTO period = new PeriodDTO(String.valueOf(periodId), "2024-1", new java.sql.Timestamp(System.currentTimeMillis()), new java.sql.Timestamp(System.currentTimeMillis() + 1000000));
        periodDAO.insertPeriod(period);

        // Group
        nrc = 12345;
        GroupDTO group = new GroupDTO(String.valueOf(nrc), "Grupo Test", String.valueOf(userId), String.valueOf(periodId));
        groupDAO.insertGroup(group);

        // Student
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

        // Project
        ProjectDTO project = new ProjectDTO(
                null,
                "Proyecto Test",
                "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId),
                organizationId
        );
        projectDAO.insertProject(project);
        projectId = projectDAO.getAllProjects().get(0).getIdProject();

        // Presentation
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1,
                projectId,
                new java.sql.Timestamp(System.currentTimeMillis()),
                Tipe.Parcial
        );
        presentationDAO.insertProjectPresentation(presentation);
        presentationId = presentationDAO.getAllProjectPresentations().get(0).getIdPresentation();
    }

    private int insertUserAndGetId(UserDTO user) throws SQLException {
        String sql = "INSERT INTO usuario (numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getStaffNumber());
            stmt.setString(2, user.getNames());
            stmt.setString(3, user.getSurnames());
            stmt.setString(4, user.getUserName());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getRole().toString());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del usuario insertado");
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
    void insertEvaluationPresentation() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0,
                presentationId,
                studentMatricula,
                new java.sql.Date(System.currentTimeMillis()),
                9.5
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        assertTrue(id > 0);

        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertNotNull(found);
        assertEquals(studentMatricula, found.getTuition());
    }

    @Test
    void updateEvaluationPresentation() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 8.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);

        EvaluationPresentationDTO toUpdate = evaluationDAO.searchEvaluationPresentationById(id);
        toUpdate.setAverage(9.0);
        boolean updated = evaluationDAO.updateEvaluationPresentation(toUpdate);
        assertTrue(updated);

        EvaluationPresentationDTO updatedEval = evaluationDAO.searchEvaluationPresentationById(id);
        assertEquals(9.0, updatedEval.getAverage());
    }

    @Test
    void deleteEvaluationPresentation() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 7.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);

        boolean deleted = evaluationDAO.deleteEvaluationPresentation(id);
        assertTrue(deleted);

        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertNull(found);
    }

    @Test
    void searchEvaluationPresentationById() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 8.5
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);

        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertNotNull(found);
        assertEquals(8.5, found.getAverage());
    }

    @Test
    void getAllEvaluationPresentations() throws Exception {
        for (int i = 0; i < 3; i++) {
            EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                    0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 7.0 + i
            );
            evaluationDAO.insertEvaluationPresentation(evaluation);
        }
        List<EvaluationPresentationDTO> list = evaluationDAO.getAllEvaluationPresentations();
        assertEquals(3, list.size());
    }

    @Test
    void getLastInsertedId() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 10.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        assertTrue(id > 0);
    }

    @Test
    void getEvaluationPresentationsByTuiton() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 9.0
        );
        evaluationDAO.insertEvaluationPresentation(evaluation);

        List<EvaluationPresentationDTO> list = evaluationDAO.getEvaluationPresentationsByTuiton(studentMatricula);
        assertFalse(list.isEmpty());
        assertEquals(studentMatricula, list.get(0).getTuition());
    }

    @Test
    void insertEvaluationPresentation_withInvalidData() {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, "NOEXISTE", new java.sql.Date(System.currentTimeMillis()), -5.0
        );
        assertThrows(Exception.class, () -> evaluationDAO.insertEvaluationPresentation(evaluation));
    }

    @Test
    void updateEvaluationPresentation_notExisting() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                9999, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 8.0
        );
        boolean updated = evaluationDAO.updateEvaluationPresentation(evaluation);
        assertFalse(updated);
    }

    @Test
    void deleteEvaluationPresentation_notExisting() throws Exception {
        boolean deleted = evaluationDAO.deleteEvaluationPresentation(9999);
        assertFalse(deleted);
    }

    @Test
    void getAllEvaluationPresentations_empty() throws Exception {
        clearTablesAndResetAutoIncrement();
        List<EvaluationPresentationDTO> list = evaluationDAO.getAllEvaluationPresentations();
        assertTrue(list.isEmpty());
    }

    @Test
    void getEvaluationPresentationsByTuiton_noResults() throws Exception {
        List<EvaluationPresentationDTO> list = evaluationDAO.getEvaluationPresentationsByTuiton("NOEXISTE");
        assertTrue(list.isEmpty());
    }

    @Test
    void insertEvaluationPresentation_nullDate() {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, null, 8.0
        );
        assertThrows(Exception.class, () -> evaluationDAO.insertEvaluationPresentation(evaluation));
    }

    @Test
    void searchEvaluationPresentationById_notExisting() throws Exception {
        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(9999);
        assertNull(found);
    }

    @Test
    void insertEvaluationPresentation_upperLimitAverage() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 10.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        assertTrue(id > 0);
        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertEquals(10.0, found.getAverage());
    }

    @Test
    void insertEvaluationPresentation_lowerLimitAverage() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 0.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        assertTrue(id > 0);
        EvaluationPresentationDTO found = evaluationDAO.searchEvaluationPresentationById(id);
        assertEquals(0.0, found.getAverage());
    }

    @Test
    void getEvaluationPresentationsByTuiton_multipleRecords() throws Exception {
        for (int i = 0; i < 3; i++) {
            EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                    0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 7.0 + i
            );
            evaluationDAO.insertEvaluationPresentation(evaluation);
        }
        List<EvaluationPresentationDTO> list = evaluationDAO.getEvaluationPresentationsByTuiton(studentMatricula);
        assertEquals(3, list.size());
    }

    @Test
    void deleteEvaluationPresentation_multipleRecords() throws Exception {
        int[] ids = new int[3];
        for (int i = 0; i < 3; i++) {
            EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                    0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 7.0 + i
            );
            ids[i] = evaluationDAO.insertEvaluationPresentation(evaluation);
        }
        boolean deleted = evaluationDAO.deleteEvaluationPresentation(ids[1]);
        assertTrue(deleted);
        assertNull(evaluationDAO.searchEvaluationPresentationById(ids[1]));
        List<EvaluationPresentationDTO> list = evaluationDAO.getAllEvaluationPresentations();
        assertEquals(2, list.size());
    }

    @Test
    void insertEvaluationPresentation_nullMatricula() {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, null, new java.sql.Date(System.currentTimeMillis()), 8.0
        );
        assertThrows(Exception.class, () -> evaluationDAO.insertEvaluationPresentation(evaluation));
    }

    @Test
    void insertEvaluationPresentation_invalidPresentationId() {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, 9999, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 8.0
        );
        assertThrows(Exception.class, () -> evaluationDAO.insertEvaluationPresentation(evaluation));
    }

    @Test
    void updateEvaluationPresentation_nullDate() throws Exception {
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.sql.Date(System.currentTimeMillis()), 8.0
        );
        int id = evaluationDAO.insertEvaluationPresentation(evaluation);
        EvaluationPresentationDTO toUpdate = evaluationDAO.searchEvaluationPresentationById(id);
        toUpdate.setDate(null);
        assertThrows(Exception.class, () -> evaluationDAO.updateEvaluationPresentation(toUpdate));
    }
}