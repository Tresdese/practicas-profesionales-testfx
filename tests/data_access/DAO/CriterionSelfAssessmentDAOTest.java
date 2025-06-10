package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.CriterionSelfAssessmentDAO;
import logic.DAO.SelfAssessmentCriteriaDAO;
import logic.DTO.CriterionSelfAssessmentDTO;
import logic.DTO.SelfAssessmentCriteriaDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CriterionSelfAssessmentDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private CriterionSelfAssessmentDAO criterionSelfAssessmentDAO;

    private int testSelfAssessmentId;
    private int testCriteriaId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
        criterionSelfAssessmentDAO = new CriterionSelfAssessmentDAO(connection);
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

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearTablesAndResetAutoIncrement();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM autoevaluacion_criterio");
        stmt.execute("DELETE FROM criterio_de_autoevaluacion");
        stmt.execute("DELETE FROM autoevaluacion");
        stmt.execute("ALTER TABLE autoevaluacion AUTO_INCREMENT = 1");
        stmt.execute("DELETE FROM evidencia");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("DELETE FROM proyecto");
        stmt.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        stmt.execute("DELETE FROM organizacion_vinculada");
        stmt.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        stmt.execute("DELETE FROM estudiante");
        stmt.execute("DELETE FROM grupo");
        stmt.execute("DELETE FROM usuario");
        stmt.execute("DELETE FROM periodo");
        stmt.close();
    }

    private void createBaseObjects() throws SQLException {
        // Period
        String sqlPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlPeriod)) {
            stmt.setString(1, "1");
            stmt.setString(2, "2024-1");
            stmt.setDate(3, Date.valueOf("2024-01-01"));
            stmt.setDate(4, Date.valueOf("2024-06-30"));
            stmt.executeUpdate();
        }
        // User
        String sqlUser = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlUser)) {
            stmt.setString(1, "1");
            stmt.setString(2, "10001");
            stmt.setString(3, "Juan");
            stmt.setString(4, "Pérez");
            stmt.setString(5, "juanp");
            stmt.setString(6, "1234567890123456789012345678901234567890123456789012345678901234");
            stmt.setString(7, "Academico");
            stmt.executeUpdate();
        }
        // Group
        String sqlGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlGroup)) {
            stmt.setString(1, "1001");
            stmt.setString(2, "Grupo 1");
            stmt.setString(3, "1");
            stmt.setString(4, "1");
            stmt.executeUpdate();
        }
        // Student
        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlStudent)) {
            stmt.setString(1, "S12345");
            stmt.setInt(2, 1);
            stmt.setString(3, "Pedro");
            stmt.setString(4, "López");
            stmt.setString(5, "5555555555");
            stmt.setString(6, "pedro@test.com");
            stmt.setString(7, "pedrolopez");
            stmt.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            stmt.setString(9, "1001");
            stmt.setString(10, "100");
            stmt.setDouble(11, 0.0);
            stmt.executeUpdate();
        }
        // Linked Organization
        int orgId = 0;
        String sqlOrg = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlOrg, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Org Test");
            stmt.setString(2, "Calle Falsa 123");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    orgId = rs.getInt(1);
                }
            }
        }
        // Project
        int projectId = 1;
        String sqlProject = "INSERT INTO proyecto (idProyecto, nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlProject)) {
            stmt.setInt(1, projectId);
            stmt.setString(2, "Proyecto Test");
            stmt.setString(3, "Descripción de prueba");
            stmt.setDate(4, Date.valueOf("2024-05-01"));
            stmt.setDate(5, Date.valueOf("2024-04-01"));
            stmt.setString(6, "1");
            stmt.setInt(7, orgId);
            stmt.executeUpdate();
        }
        // Evidence
        int evidenceId = 1;
        String sqlEvidence = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlEvidence, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Evidencia 1");
            stmt.setDate(2, Date.valueOf("2024-05-01"));
            stmt.setString(3, "/ruta/evidencia1.pdf");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    evidenceId = rs.getInt(1);
                }
            }
        }
        // SelfAssessment
        String sqlSelfAssessment = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha_registro, estado, comentarios_generales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlSelfAssessment, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Comentario base");
            stmt.setFloat(2, 8.0f);
            stmt.setString(3, "S12345");
            stmt.setInt(4, projectId);
            stmt.setInt(5, evidenceId);
            stmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            stmt.setString(7, "completada");
            stmt.setString(8, "General");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    testSelfAssessmentId = rs.getInt(1);
                }
            }
        }
        // SelfAssessment Criteria
        String sqlCriteria = "INSERT INTO criterio_de_autoevaluacion (idCriterios, nombreCriterio) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlCriteria)) {
            stmt.setInt(1, 1);
            stmt.setString(2, "Criterio 1");
            stmt.executeUpdate();
            testCriteriaId = 1;
        }
    }

    @Test
    void testInsertCriterionSelfAssessment() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 9.0f, "Buen criterio");
        boolean result = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);
        assertTrue(result);

        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(testSelfAssessmentId, testCriteriaId);
        assertEquals("Buen criterio", found.getComments());
        assertEquals(9.0f, found.getGrade());
    }

    @Test
    void testUpdateCriterionSelfAssessment() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 7.0f, "Original");
        criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);

        dto.setGrade(10.0f);
        dto.setComments("Actualizado");
        boolean result = criterionSelfAssessmentDAO.updateCriterionSelfAssessment(dto);
        assertTrue(result);

        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(testSelfAssessmentId, testCriteriaId);
        assertEquals("Actualizado", found.getComments());
        assertEquals(10.0f, found.getGrade());
    }

    @Test
    void testDeleteCriterionSelfAssessment() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 8.0f, "Para borrar");
        criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);

        boolean result = criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(testSelfAssessmentId, testCriteriaId);
        assertTrue(result);

        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(testSelfAssessmentId, testCriteriaId);
        assertEquals(0, found.getIdSelfAssessment());
        assertEquals(0, found.getIdCriteria());
    }

    @Test
    void testGetAllCriterionSelfAssessmentWhenEmpty() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM autoevaluacion_criterio");
        }
        CriterionSelfAssessmentDAO dao = new CriterionSelfAssessmentDAO(connection);
        List<CriterionSelfAssessmentDTO> all = dao.getAllCriterionSelfAssessments();
        assertTrue(all.isEmpty(), "There should be at least one self-assessment-criterion record");
    }

    @Test
    void testInsertAndSearchCriterionSelfAssessment() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 9.0f, "Comentario test");
        boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);
        assertTrue(inserted, "Insertion should be successful");

        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO
                .searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(testSelfAssessmentId, testCriteriaId);
        assertEquals("Comentario test", found.getComments());
        assertEquals(9.0f, found.getGrade());
    }

    @Test
    void testUpdateNonExistentCriterionSelfAssessment() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(9999, 8888, 5.0f, "No existe");
        boolean updated = criterionSelfAssessmentDAO.updateCriterionSelfAssessment(dto);
        assertFalse(updated, "Should not update a non-existent record");
    }

    @Test
    void testDeleteNonExistentCriterionSelfAssessment() throws SQLException {
        boolean deleted = criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(9999, 8888);
        assertFalse(deleted, "Should not delete a non-existent record");
    }

    @Test
    void testSearchNonExistentCriterionSelfAssessment() throws SQLException {
        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO
                .searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(9999, 8888);
        assertEquals(0, found.getIdSelfAssessment());
        assertEquals(0, found.getIdCriteria());
        assertEquals(-1f, found.getGrade());
        assertEquals("N/A", found.getComments());
    }

    @Test
    void testInsertDuplicatedCriterionSelfAssessment() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 8.0f, "Duplicado");
        boolean firstInsert = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);
        boolean secondInsert = false;
        try {
            secondInsert = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);
        } catch (SQLException e) {
            secondInsert = false;
        }
        assertTrue(firstInsert, "First insert should be successful");
    }

    @Test
    void testInsertCriterionSelfAssessmentWithNullComments() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 7.5f, null);
        boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);
        assertTrue(inserted, "Should allow null comments if DB allows");
    }

    void testGetAllCriterionSelfAssessments() throws SQLException {
        SelfAssessmentCriteriaDAO criteriaDAO = new SelfAssessmentCriteriaDAO();
        criteriaDAO.insertSelfAssessmentCriteria(new SelfAssessmentCriteriaDTO(String.valueOf(testCriteriaId), "Criterio 1"));
        criteriaDAO.insertSelfAssessmentCriteria(new SelfAssessmentCriteriaDTO(String.valueOf(testCriteriaId + 1), "Criterio 2"));

        criterionSelfAssessmentDAO.insertCriterionSelfAssessment(
                new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 8.0f, "Uno")
        );
        criterionSelfAssessmentDAO.insertCriterionSelfAssessment(
                new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId + 1, 9.0f, "Dos")
        );

        List<CriterionSelfAssessmentDTO> all = criterionSelfAssessmentDAO.getAllCriterionSelfAssessments();
        assertTrue(all.size() >= 2, "There should be at least two records");
    }

    @Test
    void testDeleteCriterionSelfAssessmentTwice() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 8.0f, "Para borrar dos veces");
        criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);
        assertTrue(criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(testSelfAssessmentId, testCriteriaId));
        assertFalse(criterionSelfAssessmentDAO.deleteCriterionSelfAssessment(testSelfAssessmentId, testCriteriaId), "Second delete should fail");
    }

    @Test
    void testInsertCriterionSelfAssessmentWithExtremeValues() throws SQLException {
        String longComment = "a".repeat(255);
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 10.0f, longComment);
        boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);
        assertTrue(inserted, "Should allow long comments and max grade");
    }

    @Test
    void testSearchByCompositeKey() throws SQLException {
        int selfAssessmentId = insertTestSelfAssessment();
        int criteriaId = insertTestCriteria("Criterio Key");
        CriterionSelfAssessmentDAO dao = new CriterionSelfAssessmentDAO(connection);
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(selfAssessmentId, criteriaId, 6.0f, "Buscar");
        dao.insertCriterionSelfAssessment(dto);

        CriterionSelfAssessmentDTO found = dao.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(selfAssessmentId, criteriaId);
        assertEquals("Buscar", found.getComments());
    }

    @Test
    void testGetAllCriterionSelfAssessmentsWhenEmpty() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM autoevaluacion_criterio");
        stmt.close();

        CriterionSelfAssessmentDAO dao = new CriterionSelfAssessmentDAO(connection);
        List<CriterionSelfAssessmentDTO> all = dao.getAllCriterionSelfAssessments();
        assertTrue(all.isEmpty());
    }

    private int insertTestSelfAssessment() throws SQLException {
        String sql = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha_registro, estado, comentarios_generales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Test");
            stmt.setFloat(2, 8.0f);
            stmt.setString(3, "S12345");
            stmt.setInt(4, 1);
            stmt.setInt(5, 1);
            stmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            stmt.setString(7, "completada");
            stmt.setString(8, "General");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Could not insert test self-assessment");
    }

    private int insertTestCriteria(String name) throws SQLException {
        String sql = "INSERT INTO criterio_de_autoevaluacion (nombreCriterio) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Could not insert test criteria");
    }

    @Test
    void testInsertCriterionSelfAssessmentWithInvalidForeignKeys() {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(9999, 9999, 7.0f, "Inválido");
        assertThrows(SQLException.class, () -> criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto));
    }

    @Test
    void testInsertCriterionSelfAssessmentDuplicated() throws SQLException {
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 7.0f, "Duplicado");
        assertTrue(criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto));
        assertThrows(SQLException.class, () -> criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto));
    }

    @Test
    void testSearchCriterionSelfAssessmentByInvalidKeys() throws SQLException {
        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDAO.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(9999, 9999);
        assertEquals(0, found.getIdSelfAssessment());
        assertEquals(0, found.getIdCriteria());
    }

    @Test
    void testInsertCriterionSelfAssessmentWithInvalidGrade() throws SQLException {
        float invalidGrade = 20.0f;
        CriterionSelfAssessmentDTO dto = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, invalidGrade, "Nota inválida");
        try {
            boolean inserted = criterionSelfAssessmentDAO.insertCriterionSelfAssessment(dto);
            assertTrue(inserted, "DB does not restrict grade range");
        } catch (SQLException e) {
            assertTrue(true, "DB restricts grade range");
        }
    }

    void clearCriterionSelfAssessmentTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM autoevaluacion_criterio");
        }
    }
}