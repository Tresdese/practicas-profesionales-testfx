package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LinkedOrganizationDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private LinkedOrganizationDAO organizationDAO;

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
    void setUp() { organizationDAO = new LinkedOrganizationDAO(connection); }

    private String insertTestOrganization(String idOrganization, String nombre, String direccion) throws SQLException {
        LinkedOrganizationDTO existingOrganization = organizationDAO.searchLinkedOrganizationById(idOrganization);
        if (existingOrganization != null) {
            return idOrganization;
        }

        String sql = "INSERT INTO organizacion_vinculada (idOrganizacion, nombre, direccion) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, idOrganization);
            stmt.setString(2, nombre);
            stmt.setString(3, direccion);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return String.valueOf(rs.getInt(1));
                } else {
                    throw new SQLException("No se generó ID para la organización");
                }
            }
        }
    }

    @Test
    void testInsertLinkedOrganization() {
        try {
            String uniqueName = "Organización Test " + UUID.randomUUID().toString().substring(0, 5);

            LinkedOrganizationDTO organization = new LinkedOrganizationDTO(
                    "0",
                    uniqueName,
                    "Dirección Test 123"
            );

            boolean result = organizationDAO.insertLinkedOrganization(organization);
            assertTrue(result, "La inserción debería ser exitosa");

            assertNotNull(organization.getIddOrganization(), "El ID generado no debería ser nulo");

            LinkedOrganizationDTO insertedOrg = organizationDAO.searchLinkedOrganizationById(organization.getIddOrganization());
            assertNotNull(insertedOrg, "La organización debería existir en la base de datos");
            assertEquals(uniqueName, insertedOrg.getName(), "El nombre debería coincidir");
            assertEquals("Dirección Test 123", insertedOrg.getAddress(), "La dirección debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertLinkedOrganization: " + e.getMessage());
        }
    }

    @Test
    void testGetLinkedOrganization() {
        try {
            String uniqueName = "Organización Consulta " + UUID.randomUUID().toString().substring(0, 5);
            String testOrganizationId = insertTestOrganization(UUID.randomUUID().toString(), uniqueName, "Dirección Consulta");

            LinkedOrganizationDTO org = organizationDAO.searchLinkedOrganizationById(String.valueOf(testOrganizationId));
            assertNotNull(org, "Debería encontrar la organización");
            assertEquals(uniqueName, org.getName(), "El nombre debería coincidir");
            assertEquals("Dirección Consulta", org.getAddress(), "La dirección debería coincidir");
        } catch (SQLException e) {
            fail("Error en testGetLinkedOrganization: " + e.getMessage());
        }
    }

    @Test
    void testUpdateLinkedOrganization() {
        try {
            String uniqueName = "Organización Update " + UUID.randomUUID().toString().substring(0, 5);
            String testOrganizationId = insertTestOrganization(UUID.randomUUID().toString(), uniqueName, "Dirección Original");

            LinkedOrganizationDTO orgToUpdate = new LinkedOrganizationDTO(
                    String.valueOf(testOrganizationId),
                    "Nombre Actualizado",
                    "Dirección Actualizada"
            );

            boolean updateResult = organizationDAO.updateLinkedOrganization(orgToUpdate);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            LinkedOrganizationDTO updatedOrg = organizationDAO.searchLinkedOrganizationById(String.valueOf(testOrganizationId));
            assertNotNull(updatedOrg, "La organización debería existir después de actualizar");
            assertEquals("Nombre Actualizado", updatedOrg.getName(), "El nombre debería actualizarse");
            assertEquals("Dirección Actualizada", updatedOrg.getAddress(), "La dirección debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateLinkedOrganization: " + e.getMessage());
        }
    }

    @Test
    void testGetAllLinkedOrganizations() {
        try {
            String uniqueName = "Organización Lista " + UUID.randomUUID().toString().substring(0, 5);
            String testOrganizationId = insertTestOrganization(UUID.randomUUID().toString(), uniqueName, "Dirección Lista");

            List<LinkedOrganizationDTO> organizations = organizationDAO.getAllLinkedOrganizations();
            assertNotNull(organizations, "La lista no debería ser nula");
            assertFalse(organizations.isEmpty(), "La lista no debería estar vacía");

            boolean found = organizations.stream()
                    .anyMatch(org -> org.getIddOrganization().equals(String.valueOf(testOrganizationId)));
            assertTrue(found, "La organización de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllLinkedOrganizations: " + e.getMessage());
        }
    }

    @Test
    void testDeleteLinkedOrganization() {
        try {
            String uniqueName = "Organización Delete " + UUID.randomUUID().toString().substring(0, 5);
            String deleteId = insertTestOrganization(UUID.randomUUID().toString(), uniqueName, "Dirección Delete");

            LinkedOrganizationDTO beforeDelete = organizationDAO.searchLinkedOrganizationById(String.valueOf(deleteId));
            assertNotNull(beforeDelete, "La organización debería existir antes de eliminarla");

            boolean deleteResult = organizationDAO.deleteLinkedOrganization(String.valueOf(deleteId));
            assertTrue(deleteResult, "La eliminación debería ser exitosa");

            LinkedOrganizationDTO afterDelete = organizationDAO.searchLinkedOrganizationById(String.valueOf(deleteId));
            assertNull(afterDelete, "La organización no debería existir después de eliminarla");
        } catch (SQLException e) {
            fail("Error en testDeleteLinkedOrganization: " + e.getMessage());
        }
    }
}