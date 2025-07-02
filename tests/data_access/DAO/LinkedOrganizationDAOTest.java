package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.LinkedOrganizationDAO;
import logic.DTO.LinkedOrganizationDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkedOrganizationDAOTest {

    private Connection databaseConnection;
    private LinkedOrganizationDAO linkedOrganizationDataAccessObject;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        ConnectionDataBase connectionDataBase = new ConnectionDataBase();
        databaseConnection = connectionDataBase.connectDataBase();
        linkedOrganizationDataAccessObject = new LinkedOrganizationDAO();
        databaseConnection.createStatement().execute("DELETE FROM organizacion_vinculada");
    }

    @AfterEach
    void tearDown() throws SQLException {
        databaseConnection.createStatement().execute("DELETE FROM organizacion_vinculada");
        databaseConnection.close();
    }

    @Test
    void insertLinkedOrganizationAndGetIdSuccessfully() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org A", "Address A", 1);
        String generatedId = linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organization);
        assertNotNull(generatedId, "El ID generado no debería ser nulo");
        assertFalse(generatedId.isEmpty(), "El ID generado no debería estar vacío");
    }

    @Test
    void updateLinkedOrganizationSuccessfully() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org A", "Address A", 1);
        String generatedId = linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organization);
        LinkedOrganizationDTO updatedOrganization = new LinkedOrganizationDTO(generatedId, "Updated Org", "Updated Address", 1);
        boolean wasUpdated = linkedOrganizationDataAccessObject.updateLinkedOrganization(updatedOrganization);
        assertTrue(wasUpdated, "La organización debería actualizarse correctamente");
    }

    @Test
    void updateLinkedOrganizationFailsWhenNotExists() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO("999", "Nonexistent Org", "Nonexistent Address", 1);
        boolean wasUpdated = linkedOrganizationDataAccessObject.updateLinkedOrganization(organization);
        assertFalse(wasUpdated, "No debería permitir actualizar una organización inexistente");
    }

    @Test
    void deleteLinkedOrganizationSuccessfully() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org A", "Address A", 1);
        String generatedId = linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organization);
        boolean wasDeleted = linkedOrganizationDataAccessObject.deleteLinkedOrganization(generatedId);
        assertTrue(wasDeleted, "La organización debería eliminarse correctamente");
    }

    @Test
    void deleteLinkedOrganizationFailsWhenNotExists() throws SQLException, IOException {
        boolean wasDeleted = linkedOrganizationDataAccessObject.deleteLinkedOrganization("999");
        assertFalse(wasDeleted, "No debería permitir eliminar una organización inexistente");
    }

    @Test
    void searchLinkedOrganizationByIdWhenExists() throws SQLException, IOException {
        LinkedOrganizationDTO organization = new LinkedOrganizationDTO(null, "Org A", "Address A", 1);
        String generatedId = linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organization);
        LinkedOrganizationDTO foundOrganization = linkedOrganizationDataAccessObject.searchLinkedOrganizationById(generatedId);
        assertNotNull(foundOrganization, "La organización no debería ser nula");
        assertEquals(generatedId, foundOrganization.getIdOrganization());
        assertEquals("Org A", foundOrganization.getName());
        assertEquals("Address A", foundOrganization.getAddress());
    }

    @Test
    void searchLinkedOrganizationByIdWhenNotExists() throws SQLException, IOException {
        LinkedOrganizationDTO foundOrganization = linkedOrganizationDataAccessObject.searchLinkedOrganizationById("999");
        assertNull(foundOrganization, "La organización debería ser nula si no existe");
    }

    @Test
    void getAllLinkedOrganizationsReturnsList() throws SQLException, IOException {
        LinkedOrganizationDTO organizationOne = new LinkedOrganizationDTO(null, "Org A", "Address A", 1);
        LinkedOrganizationDTO organizationTwo = new LinkedOrganizationDTO(null, "Org B", "Address B", 1);
        linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organizationOne);
        linkedOrganizationDataAccessObject.insertLinkedOrganizationAndGetId(organizationTwo);
        List<LinkedOrganizationDTO> organizationList = linkedOrganizationDataAccessObject.getAllLinkedOrganizations();
        assertNotNull(organizationList, "La lista de organizaciones no debería ser nula");
        assertEquals(2, organizationList.size(), "Debería haber 2 organizaciones en la lista");
    }

    @Test
    void getAllLinkedOrganizationsReturnsEmptyListWhenNoOrganizationsExist() throws SQLException, IOException {
        List<LinkedOrganizationDTO> organizationList = linkedOrganizationDataAccessObject.getAllLinkedOrganizations();
        assertNotNull(organizationList, "La lista de organizaciones no debería ser nula");
        assertTrue(organizationList.isEmpty(), "La lista de organizaciones debería estar vacía");
    }
}