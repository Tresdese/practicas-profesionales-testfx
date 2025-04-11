package data_access.DAO;

import java.util.Date;
import data_access.ConecctionDataBase;
import logic.DTO.EvidenceDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private EvidenceDAO evidenceDAO;
    private int testEvidenceId;

    @BeforeAll
    static void setUpClass() {
        connectionDB = new ConecctionDataBase();
        try {
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        connectionDB.closeConnection();
    }

    @BeforeEach
    void setUp() {
        evidenceDAO = new EvidenceDAO();
    }

    private int insertTestEvidence(String nombreEvidencia, Date fechaEntrega, String ruta) throws SQLException {
        String sql = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nombreEvidencia);
            stmt.setDate(2, new java.sql.Date(fechaEntrega.getTime()));
            stmt.setString(3, ruta);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("No se generó ID para la evidencia");
                }
            }
        }
    }

    @Test
    void testInsertEvidence() {
        try {
            String nombreEvidencia = "Evidencia Test";
            Date fechaEntrega = new Date();
            String ruta = "/ruta/test";

            EvidenceDTO evidence = new EvidenceDTO(0, nombreEvidencia, fechaEntrega, ruta);
            boolean result = evidenceDAO.insertEvidence(evidence, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            String sql = "SELECT * FROM evidencia WHERE nombreEvidencia = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, nombreEvidencia);
                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next(), "Debería encontrar la evidencia insertada");
                    assertEquals(nombreEvidencia, rs.getString("nombreEvidencia"), "El nombre de la evidencia debería coincidir");
                    assertEquals(ruta, rs.getString("ruta"), "La ruta debería coincidir");
                }
            }
        } catch (SQLException e) {
            fail("Error en testInsertEvidence: " + e.getMessage());
        }
    }

    @Test
    void testGetEvidence() {
        try {
            String nombreEvidencia = "Evidencia Get";
            Date fechaEntrega = new Date();
            String ruta = "/ruta/get";

            testEvidenceId = insertTestEvidence(nombreEvidencia, fechaEntrega, ruta);

            EvidenceDTO evidence = evidenceDAO.getEvidence(testEvidenceId, connection);
            assertNotNull(evidence, "Debería encontrar la evidencia");
            assertEquals(nombreEvidencia, evidence.getEvidenceName(), "El nombre de la evidencia debería coincidir");
            assertEquals(ruta, evidence.getRoute(), "La ruta debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetEvidence: " + e.getMessage());
        }
    }

    @Test
    void testUpdateEvidence() {
        try {
            String nombreEvidencia = "Evidencia Update";
            Date fechaEntrega = new Date();
            String ruta = "/ruta/update";

            testEvidenceId = insertTestEvidence(nombreEvidencia, fechaEntrega, ruta);

            EvidenceDTO evidenceToUpdate = new EvidenceDTO(testEvidenceId, "Evidencia Actualizada", fechaEntrega, "/ruta/actualizada");
            boolean result = evidenceDAO.updateEvidence(evidenceToUpdate, connection);
            assertTrue(result, "La actualización debería ser exitosa");

            EvidenceDTO updatedEvidence = evidenceDAO.getEvidence(testEvidenceId, connection);
            assertNotNull(updatedEvidence, "La evidencia debería existir después de actualizar");
            assertEquals("Evidencia Actualizada", updatedEvidence.getEvidenceName(), "El nombre debería actualizarse");
            assertEquals("/ruta/actualizada", updatedEvidence.getRoute(), "La ruta debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateEvidence: " + e.getMessage());
        }
    }

    @Test
    void testDeleteEvidence() {
        try {
            String nombreEvidencia = "Evidencia Delete";
            Date fechaEntrega = new Date();
            String ruta = "/ruta/delete";

            testEvidenceId = insertTestEvidence(nombreEvidencia, fechaEntrega, ruta);

            boolean result = evidenceDAO.deleteEvidence(testEvidenceId, connection);
            assertTrue(result, "La eliminación debería ser exitosa");

            EvidenceDTO deletedEvidence = evidenceDAO.getEvidence(testEvidenceId, connection);
            assertNull(deletedEvidence, "La evidencia eliminada no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteEvidence: " + e.getMessage());
        }
    }

    @Test
    void testGetAllEvidences() {
        try {
            String nombreEvidencia1 = "Evidencia All 1";
            String nombreEvidencia2 = "Evidencia All 2";
            Date fechaEntrega = new Date();
            String ruta1 = "/ruta/all1";
            String ruta2 = "/ruta/all2";

            insertTestEvidence(nombreEvidencia1, fechaEntrega, ruta1);
            insertTestEvidence(nombreEvidencia2, fechaEntrega, ruta2);

            List<EvidenceDTO> evidences = evidenceDAO.getAllEvidences(connection);
            assertTrue(evidences.size() >= 2, "Debería haber al menos dos evidencias");
        } catch (SQLException e) {
            fail("Error en testGetAllEvidences: " + e.getMessage());
        }
    }
}