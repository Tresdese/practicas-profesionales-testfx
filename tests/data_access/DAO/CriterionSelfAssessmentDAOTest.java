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
    private UserDAO userDAO;

    private final String periodIdBase = "1001";
    private final String groupIdBase = "2001";
    private final String studentIdBase = "3001";
    private final int evidenceIdBase = 4001;
    private final String criteriaIdBase = "5001";
    private final String selfAssessmentIdBase = "6001";
    private final String userIdBase = "1";

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
        userDAO = new UserDAO();
        clearTablesAndResetAutoIncrement();
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
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("ALTER TABLE criterio_de_autoevaluacion AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE autoevaluacion AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void createBaseRecords() throws SQLException {
        // 1. Insertar periodo
        PeriodDTO period = new PeriodDTO(periodIdBase, "Test Period",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        assertTrue(periodDAO.insertPeriod(period), "No se pudo insertar el periodo");

        // 2. Insertar usuario
        UserDTO user = new UserDTO(userIdBase, "12345", "Nombre", "Apellido",
                "testuser", "1234567890123456789012345678901234567890123456789012345678901234",
                Role.ACADEMICO);
        assertTrue(userDAO.insertUser(user), "No se pudo insertar el usuario");

        // 3. Insertar grupo
        GroupDTO group = new GroupDTO(groupIdBase, "Test Group", userIdBase, periodIdBase);
        assertTrue(groupDAO.insertGroup(group), "No se pudo insertar el grupo");

        // 4. Insertar estudiante
        StudentDTO student = new StudentDTO(studentIdBase, 1, "John", "Doe", "1234567890", "john.doe@test.com",
                "johnuser", "1234567890123456789012345678901234567890123456789012345678901234",
                groupIdBase, "80", 9.5);
        assertTrue(studentDAO.insertStudent(student), "No se pudo insertar el estudiante");

        // 5. Insertar evidencia
        EvidenceDTO evidence = new EvidenceDTO(0, "Test Evidence", new Date(), "/path/evidence.pdf");
        assertTrue(evidenceDAO.insertEvidence(evidence), "No se pudo insertar la evidencia");

        // 6. Insertar criterio de autoevaluación
        SelfAssessmentCriteriaDTO criteria = new SelfAssessmentCriteriaDTO(criteriaIdBase, "Criterio de prueba");
        assertTrue(selfAssessmentCriteriaDAO.insertSelfAssessmentCriteria(criteria),
                "No se pudo insertar el criterio de autoevaluación");

        // 7. Insertar autoevaluación
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                Integer.parseInt(selfAssessmentIdBase), // selfAssessmentId
                "",                                     // comments
                10.0f,                                  // grade
                studentIdBase,                          // registration
                Integer.parseInt(userIdBase),            // projectId
                evidenceIdBase,                         // evidenceId
                new java.util.Date(),                    // registrationDate
                SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, // status
                ""                                      // generalComments
        );
        assertTrue(selfAssessmentDAO.insertSelfAssessment(selfAssessment),
                "No se pudo insertar la autoevaluación");
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseRecords();
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

    @Test
    void testInsertCriterionSelfAssessment() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment =
                    new CriterionSelfAssessmentDTO(
                            Integer.parseInt(selfAssessmentIdBase),
                            Integer.parseInt(criteriaIdBase),
                            0.0f,
                            ""
                    );
            boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);
            assertTrue(inserted, "El registro debe insertarse correctamente");
        } catch (SQLException e) {
            fail("Error en testInsertCriterionSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testSearchCriterionSelfAssessmentByIds() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment =
                    new CriterionSelfAssessmentDTO(
                            Integer.parseInt(selfAssessmentIdBase),
                            Integer.parseInt(criteriaIdBase),
                            0.0f,
                            ""
                    );
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);

            CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO
                    .searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(
                            Integer.parseInt(selfAssessmentIdBase),
                            Integer.parseInt(criteriaIdBase)
                    );
            assertEquals(criterionSelfAssessment, found, "El registro buscado debe coincidir con el insertado");
        } catch (SQLException e) {
            fail("Error en testSearchCriterionSelfAssessmentByIds: " + e.getMessage());
        }
    }

    @Test
    void testDeleteCriterionSelfAssessment() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment =
                    new CriterionSelfAssessmentDTO(
                            Integer.parseInt(selfAssessmentIdBase),
                            Integer.parseInt(criteriaIdBase),
                            0.0f,
                            ""
                    );
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);

            boolean deleted = criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(
                    Integer.parseInt(selfAssessmentIdBase),
                    Integer.parseInt(criteriaIdBase)
            );
            assertTrue(deleted, "El registro debe eliminarse correctamente");
        } catch (SQLException e) {
            fail("Error en testDeleteCriterionSelfAssessment: " + e.getMessage());
        }
    }

    @Test
    void testGetAllCriterionSelfAssessments() {
        try {
            CriterionSelfAssessmentDTO criterionSelfAssessment =
                    new CriterionSelfAssessmentDTO(
                            Integer.parseInt(selfAssessmentIdBase),
                            Integer.parseInt(criteriaIdBase),
                            0.0f,
                            ""
                    );
            criterionSelfAssessmentDAO.insertCriterionSelfAssessment(criterionSelfAssessment);

            List<CriterionSelfAssessmentDTO> all = criterionSelfAssessmentDAO.getAllCriterionSelfAssessments();
            assertNotNull(all, "La lista no debe ser nula");
            assertFalse(all.isEmpty(), "La lista no debe estar vacía");
        } catch (SQLException e) {
            fail("Error en testGetAllCriterionSelfAssessments: " + e.getMessage());
        }
    }
}