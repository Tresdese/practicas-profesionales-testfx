package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.*;
import logic.DTO.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PartialEvaluationDAOTest {
    private Connection connection;
    private ConecctionDataBase db;
    private PartialEvaluationDAO partialEvaluationDAO;
    private GroupDAO groupDAO;
    private StudentDAO studentDAO;
    private EvidenceDAO evidenceDAO;
    private PeriodDAO periodDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setUpAll() throws Exception {
        db = new ConecctionDataBase();
        connection = db.connectDB();
        partialEvaluationDAO = new PartialEvaluationDAO();
        groupDAO = new GroupDAO();
        studentDAO = new StudentDAO();
        evidenceDAO = new EvidenceDAO();
        periodDAO = new PeriodDAO();
        userDAO = new UserDAO();
        limpiarTablasYResetearAutoIncrement();
        crearObjetosBase();
    }

    @BeforeEach
    void setUp() throws Exception {
        limpiarTablasYResetearAutoIncrement();
        crearObjetosBase();
    }

    @AfterAll
    void tearDownAll() throws Exception {
        limpiarTablasYResetearAutoIncrement();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (db != null) {
            db.close();
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        limpiarTablasYResetearAutoIncrement();
    }

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
        stmt.execute("TRUNCATE TABLE evaluacion_parcial");
        stmt.execute("TRUNCATE TABLE estudiante");
        stmt.execute("TRUNCATE TABLE evidencia");
        stmt.execute("TRUNCATE TABLE grupo");
        stmt.execute("TRUNCATE TABLE periodo");
        stmt.execute("TRUNCATE TABLE usuario");
        stmt.execute("ALTER TABLE evaluacion_parcial AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE estudiante AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("SET FOREIGN_KEY_CHECKS=1");
        stmt.close();
    }

    private void crearObjetosBase() throws SQLException {
        // 1. Period
        PeriodDTO period = new PeriodDTO("1", "Test Period", Timestamp.valueOf("2024-01-01 00:00:00"), Timestamp.valueOf("2024-12-31 00:00:00"));
        periodDAO.insertPeriod(period, connection);

        // 2. User (para el grupo)
        UserDTO user = new UserDTO("1", "1001", "Nombre", "Apellido", "usuarioTest", "passTest", logic.DTO.Role.ACADEMICO);
        userDAO.insertUser(user);

        // 3. Group
        GroupDTO group = new GroupDTO("123", "Test Group", "1", "1");
        groupDAO.insertGroup(group);

        // 4. Student
        StudentDTO student = new StudentDTO(
                "S123", 1, "John", "Doe", "1234567890", "john.doe@example.com",
                "johndoe", "password", "123", "50", 9.5
        );
        studentDAO.insertStudent(student);

        // 5. Evidence
        EvidenceDTO evidence = new EvidenceDTO(1, "Evidence 1", Date.valueOf("2024-06-01"), "path/to/evidence");
        evidenceDAO.insertEvidence(evidence);
    }

    @Test
    void insertPartialEvaluationSuccessfully() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("1", 90.0, "S123", "1");
        boolean result = partialEvaluationDAO.insertPartialEvaluation(evaluation);
        assertTrue(result, "La evaluación parcial debe insertarse correctamente");
    }

    @Test
    void updatePartialEvaluationSuccessfully() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("1", 90.0, "S123", "1");
        partialEvaluationDAO.insertPartialEvaluation(evaluation);
        PartialEvaluationDTO updatedEvaluation = new PartialEvaluationDTO("1", 95.0, "S123", "1");
        boolean result = partialEvaluationDAO.updatePartialEvaluation(updatedEvaluation);
        assertTrue(result, "La evaluación parcial debe actualizarse correctamente");
    }

    @Test
    void updatePartialEvaluationFailsWhenNotExists() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("999", 80.0, "S123", "1");
        boolean result = partialEvaluationDAO.updatePartialEvaluation(evaluation);
        assertFalse(result, "No debe actualizar una evaluación parcial inexistente");
    }

    @Test
    void deletePartialEvaluationSuccessfully() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("1", 90.0, "S123", "1");
        partialEvaluationDAO.insertPartialEvaluation(evaluation);
        boolean result = partialEvaluationDAO.deletePartialEvaluation("1");
        assertTrue(result, "La evaluación parcial debe eliminarse correctamente");
    }

    @Test
    void deletePartialEvaluationFailsWhenNotExists() throws SQLException {
        boolean result = partialEvaluationDAO.deletePartialEvaluation("999");
        assertFalse(result, "No debe eliminar una evaluación parcial inexistente");
    }

    @Test
    void searchPartialEvaluationByIdWhenExists() throws SQLException {
        PartialEvaluationDTO evaluation = new PartialEvaluationDTO("1", 90.0, "S123", "1");
        partialEvaluationDAO.insertPartialEvaluation(evaluation);
        PartialEvaluationDTO result = partialEvaluationDAO.searchPartialEvaluationById("1");
        assertNotNull(result, "La evaluación parcial no debe ser nula");
        assertEquals("1", result.getIdEvaluation());
        assertEquals(90.0, result.getAverage());
        assertEquals("S123", result.getTuiton());
        assertEquals("1", result.getEvidence());
    }

    @Test
    void searchPartialEvaluationByIdWhenNotExists() throws SQLException {
        PartialEvaluationDTO result = partialEvaluationDAO.searchPartialEvaluationById("999");
        assertNotNull(result, "La evaluación parcial no debe ser nula");
        assertEquals("N/A", result.getIdEvaluation());
    }

    @Test
    void getAllPartialEvaluationsReturnsList() throws SQLException {
        PartialEvaluationDTO evaluation1 = new PartialEvaluationDTO("1", 90.0, "S123", "1");
        PartialEvaluationDTO evaluation2 = new PartialEvaluationDTO("2", 85.0, "S123", "1");
        partialEvaluationDAO.insertPartialEvaluation(evaluation1);
        partialEvaluationDAO.insertPartialEvaluation(evaluation2);
        List<PartialEvaluationDTO> result = partialEvaluationDAO.getAllPartialEvaluations();
        assertNotNull(result, "La lista de evaluaciones no debe ser nula");
        assertEquals(2, result.size(), "Debe haber 2 evaluaciones en la lista");
    }

    @Test
    void getAllPartialEvaluationsReturnsEmptyListWhenNoEvaluationsExist() throws SQLException {
        List<PartialEvaluationDTO> result = partialEvaluationDAO.getAllPartialEvaluations();
        assertNotNull(result, "La lista de evaluaciones no debe ser nula");
        assertTrue(result.isEmpty(), "La lista de evaluaciones debe estar vacía");
    }
}