package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.CriterionSelfAssessmentDAO;
import logic.DTO.CriterionSelfAssessmentDTO;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CriterionSelfAssessmentDAOTest {

    private ConnectionDataBase connectionDataBase;
    private Connection databaseConnection;
    private CriterionSelfAssessmentDAO criterionSelfAssessmentDataAccessObject;

    private int testSelfAssessmentId;
    private int testCriteriaId;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDataBase = new ConnectionDataBase();
        databaseConnection = connectionDataBase.connectDataBase();
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
        criterionSelfAssessmentDataAccessObject = new CriterionSelfAssessmentDAO();
    }

    @AfterAll
    void tearDownAll() throws Exception {
        if (databaseConnection != null && !databaseConnection.isClosed()) {
            databaseConnection.close();
        }
        if (connectionDataBase != null) {
            connectionDataBase.close();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
    }

    @AfterEach
    void tearDown() throws SQLException {
        clearTablesAndResetAutoIncrement();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = databaseConnection.createStatement();
        statement.execute("DELETE FROM cronograma_actividad");
        statement.execute("DELETE FROM cronograma_de_actividades");
        statement.execute("DELETE FROM autoevaluacion_criterio");
        statement.execute("DELETE FROM criterio_de_autoevaluacion");
        statement.execute("DELETE FROM autoevaluacion");
        statement.execute("ALTER TABLE autoevaluacion AUTO_INCREMENT = 1");
        statement.execute("DELETE FROM evidencia");
        statement.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        statement.execute("DELETE FROM proyecto");
        statement.execute("ALTER TABLE proyecto AUTO_INCREMENT = 1");
        statement.execute("DELETE FROM organizacion_vinculada");
        statement.execute("ALTER TABLE organizacion_vinculada AUTO_INCREMENT = 1");
        statement.execute("DELETE FROM estudiante");
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM periodo");
        statement.close();
    }

    private void createBaseObjects() throws SQLException {
        String sqlPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlPeriod)) {
            statement.setString(1, "1");
            statement.setString(2, "2024-1");
            statement.setDate(3, Date.valueOf("2024-01-01"));
            statement.setDate(4, Date.valueOf("2024-06-30"));
            statement.executeUpdate();
        }

        String sqlUser = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlUser)) {
            statement.setString(1, "1");
            statement.setString(2, "10001");
            statement.setString(3, "Juan");
            statement.setString(4, "Pérez");
            statement.setString(5, "juanp");
            statement.setString(6, "1234567890123456789012345678901234567890123456789012345678901234");
            statement.setString(7, "Academico");
            statement.executeUpdate();
        }

        String sqlGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlGroup)) {
            statement.setString(1, "1001");
            statement.setString(2, "Grupo 1");
            statement.setString(3, "1");
            statement.setString(4, "1");
            statement.executeUpdate();
        }

        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlStudent)) {
            statement.setString(1, "S12345");
            statement.setInt(2, 1);
            statement.setString(3, "Pedro");
            statement.setString(4, "López");
            statement.setString(5, "5555555555");
            statement.setString(6, "pedro@test.com");
            statement.setString(7, "pedrolopez");
            statement.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            statement.setString(9, "1001");
            statement.setString(10, "100");
            statement.setDouble(11, 0.0);
            statement.executeUpdate();
        }

        int organizationId = 0;
        String sqlOrg = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES (?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlOrg, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Org Test");
            statement.setString(2, "Calle Falsa 123");
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    organizationId = resultSet.getInt(1);
                }
            }
        }

        int projectId = 1;
        String sqlProject = "INSERT INTO proyecto (idProyecto, nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlProject)) {
            statement.setInt(1, projectId);
            statement.setString(2, "Proyecto Test");
            statement.setString(3, "Descripción de prueba");
            statement.setDate(4, Date.valueOf("2024-05-01"));
            statement.setDate(5, Date.valueOf("2024-04-01"));
            statement.setString(6, "1");
            statement.setInt(7, organizationId);
            statement.executeUpdate();
        }

        int evidenceId = 1;
        String sqlEvidence = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlEvidence, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Evidencia 1");
            statement.setDate(2, Date.valueOf("2024-05-01"));
            statement.setString(3, "/ruta/evidencia1.pdf");
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    evidenceId = resultSet.getInt(1);
                }
            }
        }

        String sqlSelfAssessment = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha_registro, estado, comentarios_generales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlSelfAssessment, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Comentario base");
            statement.setFloat(2, 8.0f);
            statement.setString(3, "S12345");
            statement.setInt(4, projectId);
            statement.setInt(5, evidenceId);
            statement.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            statement.setString(7, "completada");
            statement.setString(8, "General");
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    testSelfAssessmentId = resultSet.getInt(1);
                }
            }
        }

        String sqlCriteria = "INSERT INTO criterio_de_autoevaluacion (idCriterios, nombreCriterio) VALUES (?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlCriteria)) {
            statement.setInt(1, 1);
            statement.setString(2, "Criterio 1");
            statement.executeUpdate();
            testCriteriaId = 1;
        }
    }

    @Test
    void testInsertCriterionSelfAssessment() throws SQLException, IOException {
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 9.0f, "Buen criterio");
        boolean wasInserted = criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);
        assertTrue(wasInserted);

        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDataAccessObject.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(testSelfAssessmentId, testCriteriaId);
        assertEquals("Buen criterio", found.getComments());
        assertEquals(9.0f, found.getGrade());
    }

    @Test
    void testUpdateCriterionSelfAssessment() throws SQLException, IOException {
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 7.0f, "Original");
        criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);

        criterionSelfAssessment.setGrade(10.0f);
        criterionSelfAssessment.setComments("Actualizado");
        boolean wasUpdated = criterionSelfAssessmentDataAccessObject.updateCriterionSelfAssessment(criterionSelfAssessment);
        assertTrue(wasUpdated);

        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDataAccessObject.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(testSelfAssessmentId, testCriteriaId);
        assertEquals("Actualizado", found.getComments());
        assertEquals(10.0f, found.getGrade());
    }

    @Test
    void testDeleteCriterionSelfAssessment() throws SQLException, IOException {
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 8.0f, "Para borrar");
        criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);

        boolean wasDeleted = criterionSelfAssessmentDataAccessObject.deleteCriterionSelfAssessment(testSelfAssessmentId, testCriteriaId);
        assertTrue(wasDeleted);

        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDataAccessObject.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(testSelfAssessmentId, testCriteriaId);
        assertEquals(0, found.getIdSelfAssessment());
        assertEquals(0, found.getIdCriteria());
    }

    @Test
    void testGetAllCriterionSelfAssessmentWhenEmpty() throws SQLException, IOException {
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("DELETE FROM autoevaluacion_criterio");
        }
        CriterionSelfAssessmentDAO dao = new CriterionSelfAssessmentDAO();
        List<CriterionSelfAssessmentDTO> all = dao.getAllCriterionSelfAssessments();
        assertTrue(all.isEmpty(), "There should be at least one self-assessment-criterion record");
    }

    @Test
    void testInsertAndSearchCriterionSelfAssessment() throws SQLException, IOException {
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 9.0f, "Comentario test");
        boolean wasInserted = criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);
        assertTrue(wasInserted, "Insertion should be successful");

        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDataAccessObject
                .searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(testSelfAssessmentId, testCriteriaId);
        assertEquals("Comentario test", found.getComments());
        assertEquals(9.0f, found.getGrade());
    }

    @Test
    void testUpdateNonExistentCriterionSelfAssessment() throws SQLException, IOException {
        CriterionSelfAssessmentDTO nonExistentCriterionSelfAssessment = new CriterionSelfAssessmentDTO(9999, 8888, 5.0f, "No existe");
        boolean wasUpdated = criterionSelfAssessmentDataAccessObject.updateCriterionSelfAssessment(nonExistentCriterionSelfAssessment);
        assertFalse(wasUpdated, "Should not update a non-existent record");
    }

    @Test
    void testDeleteNonExistentCriterionSelfAssessment() throws SQLException, IOException {
        boolean wasDeleted = criterionSelfAssessmentDataAccessObject.deleteCriterionSelfAssessment(9999, 8888);
        assertFalse(wasDeleted, "Should not delete a non-existent record");
    }

    @Test
    void testSearchNonExistentCriterionSelfAssessment() throws SQLException, IOException {
        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDataAccessObject
                .searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(9999, 8888);
        assertEquals(0, found.getIdSelfAssessment());
        assertEquals(0, found.getIdCriteria());
        assertEquals(-1f, found.getGrade());
        assertEquals("N/A", found.getComments());
    }

    @Test
    void testInsertDuplicatedCriterionSelfAssessment() throws SQLException, IOException {
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 8.0f, "Duplicado");
        boolean firstInsert = criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);
        boolean secondInsert = false;
        try {
            secondInsert = criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);
        } catch (SQLException exception) {
            secondInsert = false;
        }
        assertTrue(firstInsert, "First insert should be successful");
    }

    @Test
    void testInsertCriterionSelfAssessmentWithNullComments() throws SQLException, IOException {
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 7.5f, null);
        boolean wasInserted = criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);
        assertTrue(wasInserted, "Should allow null comments if DB allows");
    }

    @Test
    void testGetAllCriterionSelfAssessments() throws SQLException, IOException {
        int newCriteriaId1 = insertTestCriteria("Criterio 1 extra");
        int newCriteriaId2 = insertTestCriteria("Criterio 2 extra");

        criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(
                new CriterionSelfAssessmentDTO(testSelfAssessmentId, newCriteriaId1, 8.0f, "Uno")
        );
        criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(
                new CriterionSelfAssessmentDTO(testSelfAssessmentId, newCriteriaId2, 9.0f, "Dos")
        );

        List<CriterionSelfAssessmentDTO> all = criterionSelfAssessmentDataAccessObject.getAllCriterionSelfAssessments();
        assertTrue(all.size() >= 2, "Deberia haber al menos dos registros de autoevaluación de criterios");
    }

    @Test
    void testDeleteCriterionSelfAssessmentTwice() throws SQLException, IOException {
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 8.0f, "Para borrar dos veces");
        criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);
        assertTrue(criterionSelfAssessmentDataAccessObject.deleteCriterionSelfAssessment(testSelfAssessmentId, testCriteriaId));
        assertFalse(criterionSelfAssessmentDataAccessObject.deleteCriterionSelfAssessment(testSelfAssessmentId, testCriteriaId), "Second delete should fail");
    }

    @Test
    void testInsertCriterionSelfAssessmentWithExtremeValues() throws SQLException, IOException {
        String longComment = "a".repeat(255);
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 10.0f, longComment);
        boolean wasInserted = criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);
        assertTrue(wasInserted, "Should allow long comments and max grade");
    }

    @Test
    void testSearchByCompositeKey() throws SQLException, IOException {
        int selfAssessmentId = insertTestSelfAssessment();
        int criteriaId = insertTestCriteria("Criterio Key");
        CriterionSelfAssessmentDAO dao = new CriterionSelfAssessmentDAO();
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(selfAssessmentId, criteriaId, 6.0f, "Buscar");
        dao.insertCriterionSelfAssessment(criterionSelfAssessment);

        CriterionSelfAssessmentDTO found = dao.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(selfAssessmentId, criteriaId);
        assertEquals("Buscar", found.getComments());
    }

    @Test
    void testGetAllCriterionSelfAssessmentsWhenEmpty() throws SQLException, IOException {
        Statement statement = databaseConnection.createStatement();
        statement.execute("DELETE FROM autoevaluacion_criterio");
        statement.close();

        CriterionSelfAssessmentDAO dao = new CriterionSelfAssessmentDAO();
        List<CriterionSelfAssessmentDTO> all = dao.getAllCriterionSelfAssessments();
        assertTrue(all.isEmpty());
    }

    private int insertTestSelfAssessment() throws SQLException {
        String sqlQuery = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha_registro, estado, comentarios_generales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "Test");
            statement.setFloat(2, 8.0f);
            statement.setString(3, "S12345");
            statement.setInt(4, 1);
            statement.setInt(5, 1);
            statement.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            statement.setString(7, "completada");
            statement.setString(8, "General");
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("Could not insert test self-assessment");
    }

    private int insertTestCriteria(String name) throws SQLException {
        String sql = "INSERT INTO criterio_de_autoevaluacion (nombreCriterio) VALUES (?)";
        try (PreparedStatement statement = databaseConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) return resultSet.getInt(1);
            }
        }
        throw new SQLException("Could not insert test criteria");
    }

    @Test
    void testInsertCriterionSelfAssessmentWithInvalidForeignKeys() {
        CriterionSelfAssessmentDTO invalidCriterionSelfAssessment = new CriterionSelfAssessmentDTO(9999, 9999, 7.0f, "Inválido");
        assertThrows(SQLException.class, () -> criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(invalidCriterionSelfAssessment));
    }

    @Test
    void testInsertCriterionSelfAssessmentDuplicated() throws SQLException, IOException {
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, 7.0f, "Duplicado");
        assertTrue(criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment));
        assertThrows(SQLException.class, () -> criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment));
    }

    @Test
    void testSearchCriterionSelfAssessmentByInvalidKeys() throws SQLException, IOException {
        CriterionSelfAssessmentDTO found = criterionSelfAssessmentDataAccessObject.searchCriterionSelfAssessmentByIdIdSelfAssessmentAndIdCriteria(9999, 9999);
        assertEquals(0, found.getIdSelfAssessment());
        assertEquals(0, found.getIdCriteria());
    }

    @Test
    void testInsertCriterionSelfAssessmentWithInvalidGrade() {
        float invalidGrade = 20.0f;
        CriterionSelfAssessmentDTO criterionSelfAssessment = new CriterionSelfAssessmentDTO(testSelfAssessmentId, testCriteriaId, invalidGrade, "Nota inválida");
        try {
            boolean wasInserted = criterionSelfAssessmentDataAccessObject.insertCriterionSelfAssessment(criterionSelfAssessment);
            assertTrue(wasInserted, "Base de datos debería permitir notas fuera de rango");
        } catch (SQLException exception) {
            assertTrue(true, "Base de datos no permite notas fuera de rango, lo cual es correcto");
        } catch (IOException exception) {
            fail("Error cargando el archivo de configuracion: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Unexpected exception: " + exception.getMessage());
        }
    }

    void clearCriterionSelfAssessmentTable() throws SQLException {
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("DELETE FROM autoevaluacion_criterio");
        }
    }
}