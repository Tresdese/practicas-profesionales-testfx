package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CriterionSelfAssessmentDAOTest {

    private ConecctionDataBase connectionDB;
    private Connection connection;

    private PeriodDAO periodDAO;
    private GroupDAO groupDAO;
    private EvidenceDAO evidenceDAO;
    private SelfAssessmentCriteriaDAO selfAssessmentCriteriaDAO;
    private SelfAssessmentDAO selfAssessmentDAO;
    private CriterionSelfAssessmentDAO criterionSelfAssessmentDAO;
    private StudentDAO studentDAO;

    // Base IDs for records
    private final int periodIdBase = 1001;
    private final int groupIdBase = 2001;
    private final String studentIdBase = "3001";
    private final int evidenceIdBase = 4001;
    private final int criteriaIdBase = 5001;
    private final int selfAssessmentIdBase = 6001;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConecctionDataBase();
        connection = connectionDB.connectDB();
        periodDAO = new PeriodDAO();
        groupDAO = new GroupDAO();
        evidenceDAO = new EvidenceDAO();
        selfAssessmentCriteriaDAO = new SelfAssessmentCriteriaDAO();
        selfAssessmentDAO = new SelfAssessmentDAO();
        criterionSelfAssessmentDAO = new CriterionSelfAssessmentDAO(connection);
        studentDAO = new StudentDAO();
        clearTablesAndResetAutoIncrement();
    }

    @AfterAll
    void tearDownAll() throws Exception {
        clearTablesAndResetAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseRecords();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE autoevaluacion_criterio");
        stmt.execute("TRUNCATE TABLE autoevaluacion");
        stmt.execute("TRUNCATE TABLE criterio_de_autoevaluacion");
        stmt.execute("TRUNCATE TABLE evidencia");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("ALTER TABLE criterio_de_autoevaluacion AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE autoevaluacion AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private int lastInsertedEvidenceId(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT idEvidencia FROM evidencia ORDER BY idEvidencia DESC LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("idEvidencia");
            }
        }
        return -1;
    }

    private void createBaseRecords() throws SQLException {
        String periodId = String.valueOf(periodIdBase);
        PeriodDTO period = new PeriodDTO(periodId, "Test Period", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        assertTrue(periodDAO.insertPeriod(period, connection), "No se pudo insertar el periodo");

        String groupNRC = String.valueOf(groupIdBase);
        GroupDTO group = new GroupDTO(groupNRC, "Test Group", "1", periodId);
        assertTrue(groupDAO.insertGroup(group, connection), "No se pudo insertar el grupo");

        StudentDTO student = new StudentDTO(
                studentIdBase, 1, "John", "Doe", "1234567890", "john.doe@test.com",
                "johnuser", "1234567890123456789012345678901234567890123456789012345678901234",
                groupNRC, "80", 9.5
        );
        assertTrue(studentDAO.insertStudent(student), "No se pudo insertar el estudiante");

        EvidenceDTO evidence = new EvidenceDTO(0, "Test Evidence", new Date(), "/path/evidence.pdf");
        assertTrue(evidenceDAO.insertEvidence(evidence, connection), "No se pudo insertar la evidencia");
        int insertedEvidenceId = lastInsertedEvidenceId(connection);
        assertTrue(insertedEvidenceId > 0, "No se pudo obtener el id de la evidencia insertada");

        SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(
                String.valueOf(criteriaIdBase),
                "Criterio de prueba"
        );
        assertTrue(selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criteria, connection), "No se pudo insertar el criterio de autoevaluación");

        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                String.valueOf(selfAssessmentIdBase),
                String.valueOf(insertedEvidenceId),
                10.0,
                studentIdBase,
                "1"
        );
        assertTrue(selfAssessmentDAO.insertSelfAssessment(selfAssessment, connection), "No se pudo insertar la autoevaluación");
    }

    @Test
    void testInsertCriterionSelfAssessment() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(String.valueOf(selfAssessmentIdBase), String.valueOf(criteriaIdBase));
            boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            assertTrue(inserted, "El registro debe insertarse correctamente");
        } catch (SQLException e) {
            fail("Error en testInsertCriterionSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testSearchCriterionSelfAssessmentByIds() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(String.valueOf(selfAssessmentIdBase), String.valueOf(criteriaIdBase));
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO
                    .searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(String.valueOf(selfAssessmentIdBase), String.valueOf(criteriaIdBase));
            assertEquals(criterionSelfAssessment, found, "El registro buscado debe coincidir con el insertado");
        } catch (SQLException e) {
            fail("Error en testSearchCriterionSelfAssessmentByIds: " + e.getMessage());
        }
    }

    @Test
    void testDeleteCriterionSelfAssessment() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(String.valueOf(selfAssessmentIdBase), String.valueOf(criteriaIdBase));
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            boolean deleted = criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(String.valueOf(selfAssessmentIdBase), String.valueOf(criteriaIdBase));
            assertTrue(deleted, "El registro debe eliminarse correctamente");
        } catch (SQLException e) {
            fail("Error en testDeleteCriterionSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testGetAllCriterionSelfAssessments() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(String.valueOf(selfAssessmentIdBase), String.valueOf(criteriaIdBase));
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            List<CriterionSelfAssessmentDTO> all = criterionSelfAssessmentDAO.getAllCriterionSelfAssessments();
            assertNotNull(all, "La lista no debe ser nula");
            assertFalse(all.isEmpty(), "La lista no debe estar vacía");
        } catch (SQLException e) {
            fail("Error en testGetAllCriterionSelfAssessments: " + e.getMessage());
        }
    }
}