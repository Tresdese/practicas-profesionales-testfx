package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.SelfAssessmentDAO;
import logic.DTO.SelfAssessmentDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SelfAssessmentDAOTest {

    private ConecctionDataBase connectionDB;
    private Connection connection;
    private SelfAssessmentDAO selfAssessmentDAO;
    private String testPeriodId = "1";
    private String testUserId = "1";
    private String testGroupNRC = "1001";
    private String testStudentMatricula = "S12345";
    private int testEvidenceId = 1;
    private int testSelfAssessmentId = 1;

    @BeforeAll
    void setUpAll() throws Exception {
        connectionDB = new ConecctionDataBase();
        connection = connectionDB.connectDB();
        limpiarTablasYResetearAutoIncrement();
        crearObjetosBase();
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
        limpiarTablasYResetearAutoIncrement();
        crearObjetosBase();
        selfAssessmentDAO = new SelfAssessmentDAO();
    }

    @AfterEach
    void tearDown() throws Exception {
        limpiarTablasYResetearAutoIncrement();
    }

    private void limpiarTablasYResetearAutoIncrement() throws SQLException {
        Statement stmt = connection.createStatement();
        // Orden inverso de dependencias
        stmt.execute("DELETE FROM autoevaluacion");
        stmt.execute("ALTER TABLE autoevaluacion AUTO_INCREMENT = 1");
        stmt.execute("DELETE FROM evidencia");
        stmt.execute("ALTER TABLE evidencia AUTO_INCREMENT = 1");
        stmt.execute("DELETE FROM estudiante");
        stmt.execute("DELETE FROM grupo");
        stmt.execute("DELETE FROM usuario");
        stmt.execute("DELETE FROM periodo");
        // Si NRC, idUsuario, idPeriodo tienen autoincrement, resetea también
        stmt.close();
    }

    private void crearObjetosBase() throws SQLException {
        // Period
        String sqlPeriod = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlPeriod)) {
            stmt.setString(1, testPeriodId);
            stmt.setString(2, "2024-1");
            stmt.setDate(3, Date.valueOf("2024-01-01"));
            stmt.setDate(4, Date.valueOf("2024-06-30"));
            stmt.executeUpdate();
        }
        // User
        String sqlUser = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlUser)) {
            stmt.setString(1, testUserId);
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
            stmt.setString(1, testGroupNRC);
            stmt.setString(2, "Grupo 1");
            stmt.setString(3, testUserId);
            stmt.setString(4, testPeriodId);
            stmt.executeUpdate();
        }
        // Student
        String sqlStudent = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlStudent)) {
            stmt.setString(1, testStudentMatricula);
            stmt.setInt(2, 1);
            stmt.setString(3, "Pedro");
            stmt.setString(4, "López");
            stmt.setString(5, "5555555555");
            stmt.setString(6, "pedro@test.com");
            stmt.setString(7, "pedrolopez");
            stmt.setString(8, "1234567890123456789012345678901234567890123456789012345678901234");
            stmt.setString(9, testGroupNRC);
            stmt.setString(10, "100");
            stmt.setDouble(11, 0.0);
            stmt.executeUpdate();
        }
        // Evidence
        String sqlEvidence = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlEvidence, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Evidencia 1");
            stmt.setDate(2, Date.valueOf("2024-05-01"));
            stmt.setString(3, "/ruta/evidencia1.pdf");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    testEvidenceId = rs.getInt(1);
                }
            }
        }
    }

    @Test
    void testInsertSelfAssessment() throws SQLException {
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                1, // selfAssessmentId
                "Comentarios",
                9.5f, // grade
                "matricula123",
                2, // projectId
                3, // evidenceId
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
    void testSearchSelfAssessmentById() throws SQLException {
        int id = insertTestSelfAssessment("Comentario test", 8.0);
        SelfAssessmentDTO found = selfAssessmentDAO.searchSelfAssessmentById(String.valueOf(id));
        assertNotNull(found);
        assertEquals("Comentario test", found.getComments());
    }

    @Test
    void testUpdateSelfAssessment() throws SQLException {
        int id = insertTestSelfAssessment("Comentario original", 7.0);
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                1, // selfAssessmentId
                "Comentarios",
                9.5f, // grade
                "matricula123",
                2, // projectId
                3, // evidenceId
                new java.util.Date(),
                SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA,
                "Comentarios generales"
        );
        boolean result = selfAssessmentDAO.updateSelfAssessment(selfAssessment);
        assertTrue(result);

        SelfAssessmentDTO found = selfAssessmentDAO.searchSelfAssessmentById(String.valueOf(id));
        assertEquals("Comentario actualizado", found.getComments());
        assertEquals(10.0, found.getGrade());
    }

    @Test
    void testDeleteSelfAssessment() throws SQLException {
        int id = insertTestSelfAssessment("Comentario a borrar", 6.0);
        SelfAssessmentDTO selfAssessment = new SelfAssessmentDTO(
                1, // selfAssessmentId
                "Comentarios",
                9.5f, // grade
                "matricula123",
                2, // projectId
                3, // evidenceId
                new java.util.Date(),
                SelfAssessmentDTO.EstadoAutoevaluacion.COMPLETADA,
                "Comentarios generales"
        );
        boolean result = selfAssessmentDAO.deleteSelfAssessment(selfAssessment);
        assertTrue(result);

        SelfAssessmentDTO found = selfAssessmentDAO.searchSelfAssessmentById(String.valueOf(id));
        assertEquals("N/A", found.getSelfAssessmentId());
    }

    @Test
    void testGetAllSelfAssessments() throws SQLException {
        insertTestSelfAssessment("Comentario 1", 7.5);
        insertTestSelfAssessment("Comentario 2", 8.5);

        List<SelfAssessmentDTO> all = selfAssessmentDAO.getAllSelfAssessments();
        assertEquals(2, all.size());
    }

    private int insertTestSelfAssessment(String comments, double grade) throws SQLException {
        String sql = "INSERT INTO autoevaluacion (comentarios, calificacion, matricula, idEvidencia) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, comments);
            stmt.setDouble(2, grade);
            stmt.setString(3, testStudentMatricula);
            stmt.setInt(4, testEvidenceId);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo insertar la autoevaluación de prueba");
    }
}