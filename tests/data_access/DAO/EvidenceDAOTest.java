package data_access.DAO;

import java.io.IOException;
import java.util.Date;
import data_access.ConnectionDataBase;
import logic.DAO.EvidenceDAO;
import logic.DTO.EvidenceDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceDAOTest {

    private static ConnectionDataBase connectionDB;
    private static Connection connection;

    private EvidenceDAO evidenceDAO;
    private int testEvidenceId;

    @BeforeAll
    static void setUpClass() {
        try {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error al conectar a la base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado al conectar a la base de datos: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        connectionDB.close();
    }

    @BeforeEach
    void setUp() {
        evidenceDAO = new EvidenceDAO();
    }

    private int insertTestEvidence(String evidenceName, Date deliveryDate, String route) throws SQLException {
        String sql = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, evidenceName);
            stmt.setDate(2, new java.sql.Date(deliveryDate.getTime()));
            stmt.setString(3, route);
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
            String evidenceName = "Evidencia Test";
            Date deliveryDate = new Date();
            String route = "/ruta/test";

            EvidenceDTO evidence = new EvidenceDTO(0, evidenceName, deliveryDate, route);
            boolean result = evidenceDAO.insertEvidence(evidence);
            assertTrue(result, "La inserción debería ser exitosa");

            String sql = "SELECT * FROM evidencia WHERE nombreEvidencia = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, evidenceName);
                try (ResultSet rs = stmt.executeQuery()) {
                    assertTrue(rs.next(), "Debería encontrar la evidencia insertada");
                    assertEquals(evidenceName, rs.getString("nombreEvidencia"), "El nombre de la evidencia debería coincidir");
                    assertEquals(route, rs.getString("ruta"), "La ruta debería coincidir");
                }
            }
        } catch (SQLException e) {
            fail("Error en testInsertEvidence: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        }

    }

    @Test
    void testGetEvidence() {
        try {
            String evidenceName = "Evidencia Get";
            Date deliveryDate = new Date();
            String route = "/ruta/get";

            testEvidenceId = insertTestEvidence(evidenceName, deliveryDate, route);

            EvidenceDTO evidence = evidenceDAO.searchEvidenceById(testEvidenceId);
            assertNotNull(evidence, "Debería encontrar la evidencia");
            assertEquals(evidenceName, evidence.getEvidenceName(), "El nombre de la evidencia debería coincidir");
            assertEquals(route, evidence.getRoute(), "La ruta debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetEvidence: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        }
    }

    @Test
    void testUpdateEvidence() {
        try {
            String evidenceName = "Evidencia Update";
            Date deliveryDate = new Date();
            String route = "/ruta/update";
            int evidenceId = insertTestEvidence(evidenceName, deliveryDate, route);

            EvidenceDTO evidence = new EvidenceDTO(evidenceId, evidenceName, deliveryDate, route);
            evidence.setEvidenceName("Evidencia Actualizada");
            evidence.setRoute("/ruta/actualizada");
            boolean updated = evidenceDAO.updateEvidence(evidence);
            assertTrue(updated, "La actualización debería ser exitosa");

            EvidenceDTO updatedEvidence = evidenceDAO.searchEvidenceById(evidenceId);
            assertEquals("Evidencia Actualizada", updatedEvidence.getEvidenceName());
            assertEquals("/ruta/actualizada", updatedEvidence.getRoute());
        } catch (SQLException e) {
            fail("Error en testUpdateEvidence: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        }
    }

    @Test
    void testDeleteEvidence() {
        try {
            String evidenceName = "Evidencia Delete";
            Date deliveryDate = new Date();
            String route = "/ruta/delete";
            int evidenceId = insertTestEvidence(evidenceName, deliveryDate, route);

            boolean deleted = evidenceDAO.deleteEvidence(evidenceId);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            EvidenceDTO evidence = evidenceDAO.searchEvidenceById(evidenceId);
            assertEquals(-1, evidence.getIdEvidence(), "Debe retornar una evidencia inválida si el ID no existe");
        } catch (SQLException e) {
            fail("Error en testDeleteEvidence: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        }
    }

    @Test
    void testGetAllEvidences() {
        try {
            for (int i = 0; i < 2; i++) {
                insertTestEvidence("Evidencia All " + i, new Date(), "/ruta/all" + i);
            }
            List<EvidenceDTO> evidences = evidenceDAO.getAllEvidences();
            assertNotNull(evidences, "La lista no debe ser nula");
            assertTrue(evidences.size() >= 2, "Debe haber al menos dos evidencias");
        } catch (SQLException e) {
            fail("Error en testGetAllEvidences: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        }
    }

    @Test
    void testUpdateEvidence_NonExistent() throws SQLException, IOException {
        EvidenceDTO evidence = new EvidenceDTO(9999, "No existe", new Date(), "/ruta/noexiste");
        boolean updated = evidenceDAO.updateEvidence(evidence);
        assertFalse(updated, "No debe actualizar una evidencia inexistente");
    }

    @Test
    void testDeleteEvidence_NonExistent() throws SQLException, IOException {
        boolean deleted = evidenceDAO.deleteEvidence(9999);
        assertFalse(deleted, "No debe eliminar una evidencia inexistente");
    }

    @Test
    void testSearchEvidenceById_NonExistent() throws SQLException, IOException {
        EvidenceDTO evidence = evidenceDAO.searchEvidenceById(9999);
        assertNotNull(evidence, "Debe retornar un objeto");
        assertEquals(-1, evidence.getIdEvidence(), "Debe retornar una evidencia inválida si el ID no existe");
    }

    @Test
    void testGetAllEvidences_EmptyTable() throws SQLException, IOException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM evidencia");
        }
        List<EvidenceDTO> evidences = evidenceDAO.getAllEvidences();
        assertNotNull(evidences);
        assertTrue(evidences.isEmpty(), "La lista debe estar vacía si no hay evidencias");
    }

    @Test
    void testInsertAndRetrieveMultipleEvidences() throws SQLException, IOException {
        for (int i = 0; i < 3; i++) {
            EvidenceDTO evidence = new EvidenceDTO(0, "Evidencia" + i, new Date(), "/ruta/" + i);
            evidenceDAO.insertEvidence(evidence);
        }
        List<EvidenceDTO> evidences = evidenceDAO.getAllEvidences();
        assertTrue(evidences.size() >= 3, "Debe haber al menos tres evidencias");
    }

    @Test
    void testGetNextEvidenceId() throws SQLException, IOException {
        int nextIdBefore = evidenceDAO.getNextEvidenceId();
        insertTestEvidence("Evidencia NextId", new Date(), "/ruta/nextid");
        int nextIdAfter = evidenceDAO.getNextEvidenceId();
        assertEquals(nextIdBefore + 1, nextIdAfter, "El siguiente ID debe incrementarse en 1");
    }
}