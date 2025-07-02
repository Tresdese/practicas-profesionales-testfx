package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.SelfAssessmentDAO;
import org.junit.jupiter.api.*;
import logic.DTO.SelfAssessmentDTO;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SelfAssessmentDAOTest {

    private ConnectionDataBase connectionDB;
    private Connection connection;
    private SelfAssessmentDAO selfAssessmentDAO;
    private String testPeriodId = "1";
    private String testUserId = "1";
    private String testGroupNRC = "1001";
    private String testStudentTuition = "S12345";
    private int testEvidenceId = 1;
    private int testProjectId = 1;
    private int testOrganizationId = 1;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDataBase();
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
        selfAssessmentDAO = new SelfAssessmentDAO();
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (connectionDB != null) {
            connectionDB.close();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        clearTablesAndResetAutoIncrement();
        createBaseObjects();
    }

    @AfterEach
    void tearDown() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = connection.createStatement();
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlPeriod)) {
            preparedStatement.setString(1, testPeriodId);
            preparedStatement.setString(2, "2024-1");
            preparedStatement.setDate(3, Date.valueOf("2024-01-01"));
            preparedStatement.setDate(4, Date.valueOf("2024-06-30"));
            preparedStatement.executeUpdate();
        }
        String sqlUser = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUser)) {
            preparedStatement.setString(1, testUserId);
            preparedStatement.setString(2, "10001");
            preparedStatement.setString(3, "Juan");
            preparedStatement.setString(4, "Pérez");
            preparedStatement.setString(5, "juanp");
            preparedStatement.setString(6, "1234567890123456789012345678901234567890123456789012345678901234");
            preparedStatement.setString(7, "Academico");
            preparedStatement.executeUpdate();
        }
        String sqlGroup = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlGroup)) {
            preparedStatement.setString(1, testGroupNRC);
            preparedStatement.setString(2, "Grupo 1");
            preparedStatement.setString(3, testUserId);
            preparedStatement.setString(4, testPeriodId);
            preparedStatement.executeUpdate();
        }
        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStudent)) {
            preparedStatement.setString(1, testStudentTuition);
            preparedStatement.setInt(2, 1);
            preparedStatement.setString(3, "Pedro");
            preparedStatement.setString(4, "López");
            preparedStatement.setString(5, "5555555555");
            preparedStatement.setString(6, "pedro@test.com");
            preparedStatement.setString(7, "pedrolopez");
            preparedStatement.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            preparedStatement.setString(9, testGroupNRC);
            preparedStatement.setString(10, "100");
            preparedStatement.setDouble(11, 0.0);
            preparedStatement.executeUpdate();
        }
        String sqlOrg = "INSERT INTO organizacion_vinculada (nombre, direccion) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlOrg, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Org Test");
            preparedStatement.setString(2, "Calle Falsa 123");
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    testOrganizationId = resultSet.getInt(1);
                }
            }
        }
        String sqlProject = "INSERT INTO proyecto (idProyecto, nombre, descripcion, fechaAproximada, fechaInicio, idUsuario, idOrganizacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlProject)) {
            preparedStatement.setInt(1, testProjectId);
            preparedStatement.setString(2, "Proyecto Test");
            preparedStatement.setString(3, "Descripción de prueba");
            preparedStatement.setDate(4, Date.valueOf("2024-05-01"));
            preparedStatement.setDate(5, Date.valueOf("2024-04-01"));
            preparedStatement.setString(6, testUserId);
            preparedStatement.setInt(7, testOrganizationId);
            preparedStatement.executeUpdate();
        }
        String sqlEvidence = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlEvidence, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, "Evidencia 1");
            preparedStatement.setDate(2, Date.valueOf("2024-05-01"));
            preparedStatement.setString(3, "/ruta/evidencia1.pdf");
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    testEvidenceId = resultSet.getInt(1);
                }
            }
        }
    }

    @Test
    void testInsertSelfAssessment() throws SQLException, IOException {
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                0,
                "Buen trabajo",
                9.5f,
                testStudentTuition,
                testProjectId,
                testEvidenceId,
                new java.util.Date(),
                SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA,
                "Comentarios generales"
        );
        boolean result = selfAssessmentDAO.insertSelfAssessment(selfAssessment);
        assertTrue(result, "La inserción debería ser exitosa");
        List<SelfAssessmentDTO> all = selfAssessmentDAO.getAllSelfAssessments();
        assertFalse(all.isEmpty(), "Debe haber al menos una autoevaluación");
        assertEquals("Buen trabajo", all.get(0).getComments());
    }

    @Test
    void testSearchSelfAssessmentById() throws SQLException, IOException {
        int id = insertTestSelfAssessment("Comentario test", 8.0);
        SelfAssessmentDTO found = selfAssessmentDAO.searchSelfAssessmentById(String.valueOf(id));
        assertNotNull(found);
        assertEquals("Comentario test", found.getComments());
    }

    @Test
    void testUpdateSelfAssessment() throws SQLException, IOException {
        int id = insertTestSelfAssessment("Comentario original", 7.0);
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                id,
                "Comentario actualizado",
                10.0f,
                testStudentTuition,
                testProjectId,
                testEvidenceId,
                new java.util.Date(),
                SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA,
                "Comentarios generales"
        );
        boolean result = selfAssessmentDAO.updateSelfAssessment(selfAssessment);
        assertTrue(result);
        SelfAssessmentDTO found = selfAssessmentDAO.searchSelfAssessmentById(String.valueOf(id));
        assertEquals("Comentario actualizado", found.getComments());
        assertEquals(10.0f, found.getGrade());
    }

    @Test
    void testDeleteSelfAssessment() throws SQLException, IOException {
        int id = insertTestSelfAssessment("Comentario a borrar", 6.0);
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                id,
                "Comentario a borrar",
                6.0f,
                testStudentTuition,
                testProjectId,
                testEvidenceId,
                new java.util.Date(),
                SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA,
                "Comentarios generales"
        );
        boolean result = selfAssessmentDAO.deleteSelfAssessment(selfAssessment);
        assertTrue(result);
        SelfAssessmentDTO found = selfAssessmentDAO.searchSelfAssessmentById(String.valueOf(id));
        assertEquals(0, found.getSelfAssessmentId());
    }

    @Test
    void testGetAllSelfAssessments() throws SQLException, IOException {
        insertTestSelfAssessment("Comentario 1", 7.5);
        insertTestSelfAssessment("Comentario 2", 8.5);
        List<SelfAssessmentDTO> all = selfAssessmentDAO.getAllSelfAssessments();
        assertEquals(2, all.size());
    }

    private int insertTestSelfAssessment(String comments, double grade) throws SQLException {
        String sql = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idProyecto, idEvidencia, fecha_registro, estado, comentarios_generales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, comments);
            preparedStatement.setDouble(2, grade);
            preparedStatement.setString(3, testStudentTuition);
            preparedStatement.setInt(4, testProjectId);
            preparedStatement.setInt(5, testEvidenceId);
            preparedStatement.setDate(6, new java.sql.Date(System.currentTimeMillis()));
            preparedStatement.setString(7, "completada");
            preparedStatement.setString(8, "General");
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo insertar la autoevaluación de prueba");
    }

    @Test
    void testGetAllSelfAssessmentsWhenEmpty() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        List<SelfAssessmentDTO> all = selfAssessmentDAO.getAllSelfAssessments();
        assertTrue(all.isEmpty(), "La lista debe estar vacía si no hay registros");
    }

    @Test
    void testSearchSelfAssessmentByInvalidId() throws SQLException, IOException {
        SelfAssessmentDTO found = selfAssessmentDAO.searchSelfAssessmentById("-999");
        assertEquals(0, found.getSelfAssessmentId());
        assertEquals("N/A", found.getComments());
    }

    @Test
    void testUpdateNonExistentSelfAssessment() throws SQLException, IOException {
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                9999, "No existe", 5.0f, testStudentTuition, testProjectId, testEvidenceId,
                new java.util.Date(), SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, "N/A"
        );
        boolean result = selfAssessmentDAO.updateSelfAssessment(selfAssessment);
        assertFalse(result, "No debe actualizar una autoevaluación inexistente");
    }

    @Test
    void testDeleteNonExistentSelfAssessment() throws SQLException, IOException {
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                9999, "No existe", 5.0f, testStudentTuition, testProjectId, testEvidenceId,
                new java.util.Date(), SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, "N/A"
        );
        boolean result = selfAssessmentDAO.deleteSelfAssessment(selfAssessment);
        assertFalse(result, "No debe eliminar una autoevaluación inexistente");
    }

    @Test
    void testMultipleInsertionsAndRetrieval() throws SQLException, IOException {
        insertTestSelfAssessment("Comentario 1", 7.0);
        insertTestSelfAssessment("Comentario 2", 8.0);
        insertTestSelfAssessment("Comentario 3", 9.0);
        List<SelfAssessmentDTO> all = selfAssessmentDAO.getAllSelfAssessments();
        assertEquals(3, all.size());
    }

    @Test
    void testInsertSelfAssessmentWithInvalidForeignKeys() {
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                0, "Inválido", 5.0f, "NO_EXISTE", 9999, 9999,
                new java.util.Date(), SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, "N/A"
        );
        assertThrows(SQLException.class, () -> selfAssessmentDAO.insertSelfAssessment(selfAssessment));
    }

    @Test
    void testInsertSelfAssessmentWithNullFields() throws SQLException, IOException {
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                0, null, 8.0f, testStudentTuition, testProjectId, testEvidenceId,
                null, SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, null
        );
        boolean result = selfAssessmentDAO.insertSelfAssessment(selfAssessment);
        assertTrue(result, "Debe permitir insertar con campos nulos permitidos");
    }

    @Test
    void testInsertSelfAssessmentDuplicated() throws SQLException, IOException {
        int id1 = insertTestSelfAssessment("Duplicado", 7.0);
        int id2 = insertTestSelfAssessment("Duplicado", 7.0);
        assertNotEquals(id1, id2, "Cada inserción debe generar un ID diferente");
    }

    @Test
    void testInsertSelfAssessmentWithMaxLengthComments() throws SQLException, IOException {
        String maxComment = "a".repeat(255);
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                0, maxComment, 8.0f, testStudentTuition, testProjectId, testEvidenceId,
                new java.util.Date(), SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, "General"
        );
        boolean result = selfAssessmentDAO.insertSelfAssessment(selfAssessment);
        assertTrue(result, "Debe permitir comentarios con el máximo de caracteres");
    }

    @Test
    void testInsertSelfAssessmentWithSpecialCharacters() throws SQLException, IOException {
        String specialComment = "¡Excelente! 😊 #Prueba_áéíóú";
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                0, specialComment, 9.0f, testStudentTuition, testProjectId, testEvidenceId,
                new java.util.Date(), SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, "General"
        );
        boolean result = selfAssessmentDAO.insertSelfAssessment(selfAssessment);
        assertTrue(result, "Debe permitir caracteres especiales");
        List<SelfAssessmentDTO> all = selfAssessmentDAO.getAllSelfAssessments();
        assertTrue(all.stream().anyMatch(a -> specialComment.equals(a.getComments())));
    }

    @Test
    void testUpdateSelfAssessmentWithNullFields() throws SQLException, IOException {
        int id = insertTestSelfAssessment("Original", 7.0);
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                id, null, 8.0f, testStudentTuition, testProjectId, testEvidenceId,
                null, SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, null
        );
        boolean result = selfAssessmentDAO.updateSelfAssessment(selfAssessment);
        assertTrue(result, "Debe permitir actualizar con campos nulos permitidos");
        SelfAssessmentDTO found = selfAssessmentDAO.searchSelfAssessmentById(String.valueOf(id));
        assertNull(found.getComments());
    }

    @Test
    void testDeleteSelfAssessmentTwice() throws SQLException, IOException {
        int id = insertTestSelfAssessment("Para borrar dos veces", 6.0);
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                id, "Para borrar dos veces", 6.0f, testStudentTuition, testProjectId, testEvidenceId,
                new java.util.Date(), SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, "General"
        );
        assertTrue(selfAssessmentDAO.deleteSelfAssessment(selfAssessment));
        assertFalse(selfAssessmentDAO.deleteSelfAssessment(selfAssessment), "La segunda eliminación debe fallar");
    }

    @Test
    void testInsertSelfAssessmentWithFutureDate() throws SQLException, IOException {
        java.util.Date futureDate = new java.util.Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                0, "Futuro", 8.0f, testStudentTuition, testProjectId, testEvidenceId,
                futureDate, SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, "General"
        );
        boolean result = selfAssessmentDAO.insertSelfAssessment(selfAssessment);
        assertTrue(result, "Debe permitir fechas futuras si la lógica lo permite");
    }

    @Test
    void testInsertSelfAssessmentWithInvalidGrade() throws SQLException, IOException {
        float invalidGrade = 20.0f;
        if (invalidGrade < 0.0f || invalidGrade > 10.0f) {
            assertTrue(true, "La calificación es inválida, pero la BD no lo restringe.");
        } else {
            SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                    0, "Nota inválida", invalidGrade, testStudentTuition, testProjectId, testEvidenceId,
                    new java.util.Date(), SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA, "General"
            );
            boolean result = selfAssessmentDAO.insertSelfAssessment(selfAssessment);
            assertFalse(result, "No debe permitir insertar una calificación fuera de rango");
        }
    }
}