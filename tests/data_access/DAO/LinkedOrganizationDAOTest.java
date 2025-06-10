package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkedOrganizationDAOTest {
    private Connection connection;
    private LinkedOrganizationDAO linkedOrganizationDAO;

    @BeforeEach
    void setUp() throws SQLException {
        ConnectionDataBase connectionDB = new ConnectionDataBase();
        connection = connectionDB.connectDB();
        linkedOrganizationDAO = new LinkedOrganizationDAO();
        connection.createStatement().execute("DELETE FROM organizacion_vinculada");
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.createStatement().execute("DELETE FROM organizacion_vinculada");
        connection.close();
    }

    @Test
    void insertLinkedOrganizationAndGetIdSuccessfully() throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org A", "Address A");
        String generatedId = linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization);
        assertNotNull(generatedId, "El ID generado no debería ser nulo");
        assertFalse(generatedId.isEmpty(), "El ID generado no debería estar vacío");
    }

    @Test
    void updateLinkedOrganizationSuccessfully() throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org A", "Address A");
        String generatedId = linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization);
        LinkedOrganizationDTO updatedOrganization = new LinkedOrganizationDTO(generatedId, "Updated Org", "Updated Address");
        boolean result = linkedOrganizationDAO.updateLinkedOrganization(updatedOrganization);
        assertTrue(result, "La organización debería actualizarse correctamente");
    }

    @Test
    void updateLinkedOrganizationFailsWhenNotExists() throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO("999", "Nonexistent Org", "Nonexistent Address");
        boolean result = linkedOrganizationDAO.updateLinkedOrganization(organization);
        assertFalse(result, "No debería permitir actualizar una organización inexistente");
    }

    @Test
    void deleteLinkedOrganizationSuccessfully() throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org A", "Address A");
        String generatedId = linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization);
        boolean result = linkedOrganizationDAO.deleteLinkedOrganization(generatedId);
        assertTrue(result, "La organización debería eliminarse correctamente");
    }

    @Test
    void deleteLinkedOrganizationFailsWhenNotExists() throws SQLException {
        boolean result = linkedOrganizationDAO.deleteLinkedOrganization("999");
        assertFalse(result, "No debería permitir eliminar una organización inexistente");
    }

    @Test
    void searchLinkedOrganizationByIdWhenExists() throws SQLException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org A", "Address A");
        String generatedId = linkedOrganizationDAO.insertLinkedOrganizationAndGetId(organization);
        LinkedOrganizationDTO result = linkedOrganizationDAO.searchLinkedOrganizationById(generatedId);
        assertNotNull(result, "La organización no debería ser nula");
        assertEquals(generatedId, result.getIdOrganization());
        assertEquals("Org A", result.getName());
        assertEquals("Address A", result.getAddress());
    }

    @Test
    void searchLinkedOrganizationByIdWhenNotExists() throws SQLException {
        LinkedOrganizationDTO result = linkedOrganizationDAO.searchLinkedOrganizationById("999");
        assertNotNull(result, "La organización no debería ser nula");
        assertEquals("N/A", result.getIdOrganization());
        assertEquals("N/A", result.getName());
        assertEquals("N/A", result.getAddress());
    }

    @Test
    void getAllLinkedOrganizationsReturnsList() throws SQLException {
        LinkedOrganizationDTO org1 = new LinkedOrganizationDTO(null, "Org A", "Address A");
        LinkedOrganizationDTO org2 = new LinkedOrganizationDTO(null, "Org B", "Address B");
        linkedOrganizationDAO.insertLinkedOrganizationAndGetId(org1);
        linkedOrganizationDAO.insertLinkedOrganizationAndGetId(org2);
        List<LinkedOrganizationDTO> result = linkedOrganizationDAO.getAllLinkedOrganizations();
        assertNotNull(result, "La lista de organizaciones no debería ser nula");
        assertEquals(2, result.size(), "Debería haber 2 organizaciones en la lista");
    }

    @Test
    void getAllLinkedOrganizationsReturnsEmptyListWhenNoOrganizationsExist() throws SQLException {
        List<LinkedOrganizationDTO> result = linkedOrganizationDAO.getAllLinkedOrganizations();
        assertNotNull(result, "La lista de organizaciones no debería ser nula");
        assertTrue(result.isEmpty(), "La lista de organizaciones debería estar vacía");
    }
}