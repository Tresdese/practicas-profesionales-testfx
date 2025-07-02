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
    private String studentTuition;
    private int presentationId;
    private int periodId;
    private int nrc;
    private int evaluationId;
    private int criterionId;
    private int departmentId;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
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
        createDataBase();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        createDataBase();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET FOREIGN_KEY_CHECKS=0");
        statement.execute("TRUNCATE TABLE detalle_evaluacion");
        statement.execute("TRUNCATE TABLE evaluacion_presentacion");
        statement.execute("TRUNCATE TABLE presentacion_proyecto");
        statement.execute("TRUNCATE TABLE proyecto");
        statement.execute("TRUNCATE TABLE estudiante");
        statement.execute("TRUNCATE TABLE grupo");
        statement.execute("TRUNCATE TABLE periodo");
        statement.execute("TRUNCATE TABLE usuario");
        statement.execute("TRUNCATE TABLE departamento");
        statement.execute("TRUNCATE TABLE organizacion_vinculada");
        statement.execute("TRUNCATE TABLE criterio_de_evaluacion");
        statement.execute("ALTER TABLE detalle_evaluacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE evaluacion_presentacion AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE presentacion_proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE criterio_de_evaluacion AUTO_INCREMENT = 1");
        statement.execute("SET FOREIGN_KEY_CHECKS=1");
        statement.close();
    }

    private void createDataBase() throws SQLException, IOException {
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
                "A12345678", 1, "Estudiante", "Apellido", "1234567890", "correo@test.com",
                "test", "test", String.valueOf(nrc), "100", 0.0
        );
        studentDAO.insertStudent(student);
        studentTuition = "A12345678";

        ProjectDTO project = new ProjectDTO(
                null, "Proyecto Test", "Descripción Test",
                new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()),
                String.valueOf(userId), organizationId, departmentId
        );
        projectDAO.insertProject(project);
        projectId = projectDAO.getAllProjects().get(0).getIdProject();

        ProjectPresentationDTO presentation = new ProjectPresentationDTO(
                1, projectId, new java.sql.Timestamp(System.currentTimeMillis()), Type.Partial
        );
        presentationDAO.insertProjectPresentation(presentation);
        presentationId = presentationDAO.getAllProjectPresentations().get(0).getIdPresentation();

        EvaluationPresentationDTO evaluation = new EvaluationPresentationDTO(
                0, presentationId, studentTuition, new java.util.Date(),
                "Comentario de prueba", 9.5
        );
        evaluationId = evaluationDAO.insertEvaluationPresentation(evaluation);

        AssessmentCriterionDTO criterion = new AssessmentCriterionDTO(null, "Criterio Test");
        criterionDAO.insertAssessmentCriterion(criterion);
        criterionId = Integer.parseInt(criterionDAO.getAllAssessmentCriteria().get(0).getIdCriterion());
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
    void tearDown() throws SQLException {
        clearTablesAndResetAutoIncrement();
    }

    @Test
    void testInsertEvaluationDetail() throws SQLException, IOException {
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
    void testUpdateEvaluationDetail() throws SQLException, IOException {
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
    void testDeleteEvaluationDetail() throws SQLException, IOException {
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
    void testSearchEvaluationDetailById() throws SQLException, IOException {
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
    void testGetAllEvaluationDetails() throws SQLException, IOException {
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
    void testUpdateEvaluationDetailNotExisting() throws SQLException, IOException {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(9999, evaluationId, criterionId, 8.0);
        boolean updated = detailDAO.updateEvaluationDetail(detail);
        assertFalse(updated);
    }

    @Test
    void testDeleteEvaluationDetailNotExisting() throws SQLException, IOException {
        boolean deleted = detailDAO.deleteEvaluationDetail(9999);
        assertFalse(deleted);
    }

    @Test
    void testGetAllEvaluationDetailsEmpty() throws SQLException, IOException {
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
    void testInsertEvaluationDetailWithMaxGrade() throws SQLException, IOException {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 10.0);
        detailDAO.insertEvaluationDetail(detail);
        var details = detailDAO.getAllEvaluationDetails();
        assertEquals(10.0, details.get(0).getGrade());
    }

    @Test
    void testInsertEvaluationDetailWithMinGrade() throws SQLException, IOException {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 0.0);
        detailDAO.insertEvaluationDetail(detail);
        var details = detailDAO.getAllEvaluationDetails();
        assertEquals(0.0, details.get(0).getGrade());
    }

    @Test
    void testSearchEvaluationDetailByIdNotExisting() throws SQLException, IOException {
        EvaluationDetailDTO found = detailDAO.searchEvaluationDetailById(9999);
        assertEquals(-1, found.getIdDetail());
    }

    @Test
    void testMultipleEvaluationDetailsForSameEvaluation() throws SQLException, IOException {
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
    void testDeleteAllEvaluationDetails() throws SQLException, IOException {
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
    void testUpdateEvaluationDetailWithNullValues() throws SQLException, IOException {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0);
        detailDAO.insertEvaluationDetail(detail);
        EvaluationDetailDTO inserted = detailDAO.getAllEvaluationDetails().get(0);

        inserted.setGrade(Double.NaN);
        assertThrows(Exception.class, () -> detailDAO.updateEvaluationDetail(inserted));
    }

    @Test
    void testUpdateEvaluationDetailWithMaxGrade() throws SQLException, IOException {
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
    void testUpdateEvaluationDetailWithMinGrade() throws SQLException, IOException {
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
    void testSearchEvaluationDetailByInvalidId() throws SQLException, IOException {
        EvaluationDetailDTO found = detailDAO.searchEvaluationDetailById(-1);
        assertEquals(-1, found.getIdDetail());
    }

    @Test
    void testDeleteEvaluationDetailTwice() throws SQLException, IOException {
        EvaluationDetailDTO detail = new EvaluationDetailDTO(0, evaluationId, criterionId, 8.0);
        detailDAO.insertEvaluationDetail(detail);
        EvaluationDetailDTO inserted = detailDAO.getAllEvaluationDetails().get(0);

        boolean deletedFirst = detailDAO.deleteEvaluationDetail(inserted.getIdDetail());
        boolean deletedSecond = detailDAO.deleteEvaluationDetail(inserted.getIdDetail());
        assertTrue(deletedFirst);
        assertFalse(deletedSecond);
    }
}