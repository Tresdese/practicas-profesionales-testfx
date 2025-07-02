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

    private static ConnectionDataBase connectionDataBase;
    private static Connection databaseConnection;

    private EvidenceDAO evidenceDataAccessObject;
    private int testEvidenceId;

    @BeforeAll
    static void setUpClass() {
        try {
            connectionDataBase = new ConnectionDataBase();
            databaseConnection = connectionDataBase.connectDataBase();
        } catch (SQLException exception) {
            fail("Error al conectar a la base de datos: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        } catch (Exception exception) {
            fail("Error inesperado al conectar a la base de datos: " + exception.getMessage());
        }
    }

    @AfterAll
    static void tearDownClass() {
        connectionDataBase.close();
    }

    @BeforeEach
    void setUp() {
        evidenceDataAccessObject = new EvidenceDAO();
    }

    private int insertTestEvidence(String evidenceName, Date deliveryDate, String route) throws SQLException {
        String sql = "INSERT INTO evidencia (nombreEvidencia, fechaEntrega, ruta) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, evidenceName);
            preparedStatement.setDate(2, new java.sql.Date(deliveryDate.getTime()));
            preparedStatement.setString(3, route);
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
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
            boolean wasInserted = evidenceDataAccessObject.insertEvidence(evidence);
            assertTrue(wasInserted, "La inserción debería ser exitosa");

            String sql = "SELECT * FROM evidencia WHERE nombreEvidencia = ?";
            try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(sql)) {
                preparedStatement.setString(1, evidenceName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    assertTrue(resultSet.next(), "Debería encontrar la evidencia insertada");
                    assertEquals(evidenceName, resultSet.getString("nombreEvidencia"), "El nombre de la evidencia debería coincidir");
                    assertEquals(route, resultSet.getString("ruta"), "La ruta debería coincidir");
                }
            }
        } catch (SQLException exception) {
            fail("Error en testInsertEvidence: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        }
    }

    @Test
    void testGetEvidence() {
        try {
            String evidenceName = "Evidencia Get";
            Date deliveryDate = new Date();
            String route = "/ruta/get";

            testEvidenceId = insertTestEvidence(evidenceName, deliveryDate, route);

            EvidenceDTO evidence = evidenceDataAccessObject.searchEvidenceById(testEvidenceId);
            assertNotNull(evidence, "Debería encontrar la evidencia");
            assertEquals(evidenceName, evidence.getEvidenceName(), "El nombre de la evidencia debería coincidir");
            assertEquals(route, evidence.getRoute(), "La ruta debería coincidir");
        } catch (SQLException exception) {
            fail("Error en testGetEvidence: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
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
            boolean wasUpdated = evidenceDataAccessObject.updateEvidence(evidence);
            assertTrue(wasUpdated, "La actualización debería ser exitosa");

            EvidenceDTO updatedEvidence = evidenceDataAccessObject.searchEvidenceById(evidenceId);
            assertEquals("Evidencia Actualizada", updatedEvidence.getEvidenceName());
            assertEquals("/ruta/actualizada", updatedEvidence.getRoute());
        } catch (SQLException exception) {
            fail("Error en testUpdateEvidence: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        }
    }

    @Test
    void testDeleteEvidence() {
        try {
            String evidenceName = "Evidencia Delete";
            Date deliveryDate = new Date();
            String route = "/ruta/delete";
            int evidenceId = insertTestEvidence(evidenceName, deliveryDate, route);

            boolean wasDeleted = evidenceDataAccessObject.deleteEvidence(evidenceId);
            assertTrue(wasDeleted, "La eliminación debería ser exitosa");

            EvidenceDTO evidence = evidenceDataAccessObject.searchEvidenceById(evidenceId);
            assertEquals(-1, evidence.getIdEvidence(), "Debe retornar una evidencia inválida si el ID no existe");
        } catch (SQLException exception) {
            fail("Error en testDeleteEvidence: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        }
    }

    @Test
    void testGetAllEvidences() {
        try {
            for (int index = 0; index < 2; index++) {
                insertTestEvidence("Evidencia All " + index, new Date(), "/ruta/all" + index);
            }
            List<EvidenceDTO> evidences = evidenceDataAccessObject.getAllEvidences();
            assertNotNull(evidences, "La lista no debe ser nula");
            assertTrue(evidences.size() >= 2, "Debe haber al menos dos evidencias");
        } catch (SQLException exception) {
            fail("Error en testGetAllEvidences: " + exception.getMessage());
        } catch (IOException exception) {
            fail("Error al cargar la configuración de la base de datos: " + exception.getMessage());
        }
    }

    @Test
    void testUpdateEvidenceNonExistent() throws SQLException, IOException {
        EvidenceDTO evidence = new EvidenceDTO(9999, "No existe", new Date(), "/ruta/noexiste");
        boolean wasUpdated = evidenceDataAccessObject.updateEvidence(evidence);
        assertFalse(wasUpdated, "No debe actualizar una evidencia inexistente");
    }

    @Test
    void testDeleteEvidenceNonExistent() throws SQLException, IOException {
        boolean wasDeleted = evidenceDataAccessObject.deleteEvidence(9999);
        assertFalse(wasDeleted, "No debe eliminar una evidencia inexistente");
    }

    @Test
    void testSearchEvidenceByIdNonExistent() throws SQLException, IOException {
        EvidenceDTO evidence = evidenceDataAccessObject.searchEvidenceById(9999);
        assertNotNull(evidence, "Debe retornar un objeto");
        assertEquals(-1, evidence.getIdEvidence(), "Debe retornar una evidencia inválida si el ID no existe");
    }

    @Test
    void testGetAllEvidencesEmptyTable() throws SQLException, IOException {
        try (Statement statement = databaseConnection.createStatement()) {
            statement.execute("DELETE FROM evidencia");
        }
        List<EvidenceDTO> evidences = evidenceDataAccessObject.getAllEvidences();
        assertNotNull(evidences);
        assertTrue(evidences.isEmpty(), "La lista debe estar vacía si no hay evidencias");
    }

    @Test
    void testInsertAndRetrieveMultipleEvidences() throws SQLException, IOException {
        for (int index = 0; index < 3; index++) {
            EvidenceDTO evidence = new EvidenceDTO(0, "Evidencia" + index, new Date(), "/ruta/" + index);
            evidenceDataAccessObject.insertEvidence(evidence);
        }
        List<EvidenceDTO> evidences = evidenceDataAccessObject.getAllEvidences();
        assertTrue(evidences.size() >= 3, "Debe haber al menos tres evidencias");
    }

    @Test
    void testGetNextEvidenceId() throws SQLException, IOException {
        int nextIdBefore = evidenceDataAccessObject.getNextEvidenceId();
        insertTestEvidence("Evidencia NextId", new Date(), "/ruta/nextid");
        int nextIdAfter = evidenceDataAccessObject.getNextEvidenceId();
        assertEquals(nextIdBefore + 1, nextIdAfter, "El siguiente ID debe incrementarse en 1");
    }
}