package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EvaluationDetailDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private LinkedOrganizationDAO organizationDAO;
    private ProjectDAO projectDAO;
    private StudentDAO studentDAO;
    private ProjectPresentationDAO presentationDAO;
    private EvaluationPresentationDAO evaluationDAO;
    private EvaluationDetailDAO detailDAO;
    private PeriodDAO periodDAO;
    private GroupDAO groupDAO;
    private AssessmentCriterionDAO criterionDAO;

    private int userId;
    private int organizationId;
    private String projectId;
    private String studentMatricula;
    private int presentationId;
    private int periodId;
    private int nrc;
    private int evaluationId;
    private int criterionId;
    private int departmentId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        userDAO = new UserDAO();
        departmentDAO = new DepartmentDAO();
        organizationDAO = new LinkedOrganizationDAO();
        projectDAO = new ProjectDAO();
        studentDAO = new StudentDAO();
        presentationDAO = new ProjectPresentationDAO();
        evaluationDAO = new EvaluationPresentationDAO();
        detailDAO = new EvaluationDetailDAO();
        periodDAO = new PeriodDAO();
        groupDAO = new GroupDAO();
        criterionDAO = new AssessmentCriterionDAO();

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
        stmt.execute("TRUNCATE TABLE detalle_evaluacion");
        stmt.execute("TRUNCATE TABLE evaluacion_presentacion");
        stmt.execute("TRUNCATE TABLE presentacion_proyecto");
        stmt.execute("TRUNCATE TABLE proyecto");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE departamento");
        stmt.execute("TRUNCATE TABLE organizacion_vinculada");
        stmt.execute("TRUNCATE TABLE criterio_de_evaluacion");
        stmt.execute("ALTER TABLE detalle_evaluacion AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE evaluacion_presentacion AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE criterio_de_evaluacion AUTO_INCREMENT = 1");
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

        DepartmentDTO department = new DepartmentDTO(0, "Dept Test", "Descripción test", organizationId);
        departmentDAO.insertDepartment(department);
        List<DepartmentDTO> departments = departmentDAO.getAllDepartmentsByOrganizationId(organizationId);
        departmentId = departments.get(0).getDepartmentId();

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
                "A12345678", 1, "Estudiante", "Apellido", "1234567890", "correo@test.com",
                "test", "test", String.valueOf(nrc), "100", 0.0
        );
        studentDAO.insertStudent(student);
        studentMatricula = "A12345678";

        // Project
        ProjectDTO project = new ProjectDTO(
                null, "Proyecto Test", "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId), organizationId, departmentId
        );
        projectDAO.insertProject(project);
        projectId = projectDAO.getAllProjects().get(0).getIdProject();

        // Presentation
        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Tipe.Parcial
        );
        presentationDAO.insertProjectPresentation(presentation);
        presentationId = presentationDAO.getAllProjectPresentations().get(0).getIdPresentation();

        // Evaluation presentation
        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentMatricula, new java.util.Date(),
                "Comentario de prueba", 9.5
        );
        evaluationId = evaluationDAO.insertEvaluationPresentation(evaluation);

        // Assessment criterion
        AssessmentCriterionDTO criterion = new AssessmentCriterionDTO(null, "Criterio Test");
        criterionDAO.insertAssessmentCriterion(criterion);
        criterionId = Integer.parseInt(criterionDAO.getAllAssessmentCriteria().get(0).getIdCriterion());
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
    void testInsertEvaluationDetail() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(
                0,
                evaluationId,
                criterionId,
                9.0
        );
        detailDAO.insertEvaluationDetail(detail);

        var details = detailDAO.getAllEvaluationDetails();
        assertEquals(1, details.size());
        EvaluationDetailDTO inserted = details.get(0);
        assertEquals(evaluationId, inserted.getIdEvaluation());
        assertEquals(criterionId, inserted.getIdCriteria());
        assertEquals(9.0, inserted.getGrade());
    }

    @Test
    void testUpdateEvaluationDetail() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0);
        detailDAO.insertEvaluationDetail(detail);
        EvaluationDetailDTO inserted = detailDAO.getAllEvaluationDetails().get(0);

        inserted.setGrade(9.5);
        boolean updated = detailDAO.updateEvaluationDetail(inserted);
        assertTrue(updated);

        EvaluationDetailDTO updatedDetail = detailDAO.searchEvaluationDetailById(inserted.getIdDetail());
        assertEquals(9.5, updatedDetail.getGrade());
    }

    @Test
    void testDeleteEvaluationDetail() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(-1, evaluationId, criterionId, 8.0);
        detailDAO.insertEvaluationDetail(detail);

        List<EvaluationDetailDTO> all = detailDAO.getAllEvaluationDetails();
        EvaluationDetailDTO inserted = all.get(all.size() - 1);

        boolean deleted = detailDAO.deleteEvaluationDetail(inserted.getIdDetail());
        assertTrue(deleted);

        EvaluationDetailDTO result = detailDAO.searchEvaluationDetailById(inserted.getIdDetail());
        assertEquals(-1, result.getIdDetail());
    }

    @Test
    void testSearchEvaluationDetailById() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.5);
        detailDAO.insertEvaluationDetail(detail);
        EvaluationDetailDTO inserted = detailDAO.getAllEvaluationDetails().get(0);

        EvaluationDetailDTO found = detailDAO.searchEvaluationDetailById(inserted.getIdDetail());
        assertNotNull(found);
        assertEquals(8.5, found.getGrade());
        assertEquals(evaluationId, found.getIdEvaluation());
        assertEquals(criterionId, found.getIdCriteria());
    }

    @Test
    void testGetAllEvaluationDetails() throws Exception {
        for (int i = 0; i < 3; i++) {
            EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 7.0 + i);
            detailDAO.insertEvaluationDetail(detail);
        }
        var details = detailDAO.getAllEvaluationDetails();
        assertEquals(3, details.size());
    }

    @Test
    void testInsertEvaluationDetailWithInvalidData() {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, 9999, 9999, -1.0);
        assertThrows(Exception.class, () -> detailDAO.insertEvaluationDetail(detail));
    }

    @Test
    void testUpdateEvaluationDetailNotExisting() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(9999, evaluationId, criterionId, 8.0);
        boolean updated = detailDAO.updateEvaluationDetail(detail);
        assertFalse(updated);
    }

    @Test
    void testDeleteEvaluationDetailNotExisting() throws Exception {
        boolean deleted = detailDAO.deleteEvaluationDetail(9999);
        assertFalse(deleted);
    }

    @Test
    void testGetAllEvaluationDetailsEmpty() throws Exception {
        clearTablesAndResetAutoIncrement();
        var details = detailDAO.getAllEvaluationDetails();
        assertTrue(details.isEmpty());
    }

    @Test
    void testInsertEvaluationDetailWithNullValues() {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0);
        assertThrows(Exception.class, () -> detailDAO.insertEvaluationDetail(
                new EvaluationDetailDTO(0, evaluationId, 0, 8.0)
        ));
    }

    @Test
    void testInsertEvaluationDetailWithMaxGrade() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 10.0);
        detailDAO.insertEvaluationDetail(detail);
        var details = detailDAO.getAllEvaluationDetails();
        assertEquals(10.0, details.get(0).getGrade());
    }

    @Test
    void testInsertEvaluationDetailWithMinGrade() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 0.0);
        detailDAO.insertEvaluationDetail(detail);
        var details = detailDAO.getAllEvaluationDetails();
        assertEquals(0.0, details.get(0).getGrade());
    }

    @Test
    void testSearchEvaluationDetailByIdNotExisting() throws Exception {
        EvaluationDetailDTO found = detailDAO.searchEvaluationDetailById(9999);
        assertEquals(-1, found.getIdDetail());
    }

    @Test
    void testMultipleEvaluationDetailsForSameEvaluation() throws Exception {
        for (int i = 0; i < 3; i++) {
            EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 7.0 + i);
            detailDAO.insertEvaluationDetail(detail);
        }
        var details = detailDAO.getAllEvaluationDetails();
        assertEquals(3, details.size());
        for (EvaluationDetailDTO d : details) {
            assertEquals(evaluationId, d.getIdEvaluation());
        }
    }

    @Test
    void testDeleteAllEvaluationDetails() throws Exception {
        for (int i = 0; i < 2; i++) {
            EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0 + i);
            detailDAO.insertEvaluationDetail(detail);
        }
        var details = detailDAO.getAllEvaluationDetails();
        for (EvaluationDetailDTO d : details) {
            detailDAO.deleteEvaluationDetail(d.getIdDetail());
        }
        assertTrue(detailDAO.getAllEvaluationDetails().isEmpty());
    }

    @Test
    void testInsertEvaluationDetailWithNullEvaluationId() {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, 0, criterionId, 8.0);
        assertThrows(Exception.class, () -> detailDAO.insertEvaluationDetail(detail));
    }

    @Test
    void testInsertEvaluationDetailWithNullCriterionId() {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, 0, 8.0);
        assertThrows(Exception.class, () -> detailDAO.insertEvaluationDetail(detail));
    }

    @Test
    void testInsertEvaluationDetailWithNullGrade() {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, Double.NaN);
        assertThrows(Exception.class, () -> detailDAO.insertEvaluationDetail(detail));
    }

    @Test
    void testUpdateEvaluationDetailWithNullValues() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0);
        detailDAO.insertEvaluationDetail(detail);
        EvaluationDetailDTO inserted = detailDAO.getAllEvaluationDetails().get(0);

        inserted.setGrade(Double.NaN);
        assertThrows(Exception.class, () -> detailDAO.updateEvaluationDetail(inserted));
    }

    @Test
    void testUpdateEvaluationDetailWithMaxGrade() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0);
        detailDAO.insertEvaluationDetail(detail);
        EvaluationDetailDTO inserted = detailDAO.getAllEvaluationDetails().get(0);

        inserted.setGrade(10.0);
        boolean updated = detailDAO.updateEvaluationDetail(inserted);
        assertTrue(updated);
        EvaluationDetailDTO updatedDetail = detailDAO.searchEvaluationDetailById(inserted.getIdDetail());
        assertEquals(10.0, updatedDetail.getGrade());
    }

    @Test
    void testUpdateEvaluationDetailWithMinGrade() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0);
        detailDAO.insertEvaluationDetail(detail);
        EvaluationDetailDTO inserted = detailDAO.getAllEvaluationDetails().get(0);

        inserted.setGrade(0.0);
        boolean updated = detailDAO.updateEvaluationDetail(inserted);
        assertTrue(updated);
        EvaluationDetailDTO updatedDetail = detailDAO.searchEvaluationDetailById(inserted.getIdDetail());
        assertEquals(0.0, updatedDetail.getGrade());
    }

    @Test
    void testSearchEvaluationDetailByInvalidId() throws Exception {
        EvaluationDetailDTO found = detailDAO.searchEvaluationDetailById(-1);
        assertEquals(-1, found.getIdDetail());
    }

    @Test
    void testDeleteEvaluationDetailTwice() throws Exception {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0);
        detailDAO.insertEvaluationDetail(detail);
        EvaluationDetailDTO inserted = detailDAO.getAllEvaluationDetails().get(0);

        boolean deletedFirst = detailDAO.deleteEvaluationDetail(inserted.getIdDetail());
        boolean deletedSecond = detailDAO.deleteEvaluationDetail(inserted.getIdDetail());
        assertTrue(deletedFirst);
        assertFalse(deletedSecond);
    }
}