package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.GroupDAO;
import logic.DTO.GroupDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupDAOTest {
    private Connection connection;
    private GroupDAO groupDAO;

    @BeforeEach
    void setUp() throws SQLException {
        // Configura la conexión a la base de datos real
        ConecctionDataBase conecctionDataBase = new ConecctionDataBase();
        connection = conecctionDataBase.connectDB();
        groupDAO = new GroupDAO();

        // Limpia la tabla antes de cada prueba
        connection.createStatement().execute("DELETE FROM grupo");
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Limpia la tabla después de cada prueba
        connection.createStatement().execute("DELETE FROM grupo");
        connection.close();
    }

    @Test
    void insertGroupWhenGroupDoesNotExist() throws SQLException {
        GroupDTO group = new GroupDTO("123", "Group A", "User1", "Period1");

        boolean result = groupDAO.insertGroup(group, connection);

        assertTrue(result, "El grupo debería insertarse correctamente");
    }

    @Test
    void insertGroupWhenGroupAlreadyExists() throws SQLException {
        GroupDTO group = new GroupDTO("123", "Group A", "User1", "Period1");
        groupDAO.insertGroup(group, connection);

        boolean result = groupDAO.insertGroup(group, connection);

        assertFalse(result, "No debería permitir insertar un grupo con el mismo NRC");
    }

    @Test
    void updateGroupSuccessfully() throws SQLException {
        GroupDTO group = new GroupDTO("123", "Group A", "User1", "Period1");
        groupDAO.insertGroup(group, connection);

        GroupDTO updatedGroup = new GroupDTO("123", "Updated Group", "User2", "Period2");
        boolean result = groupDAO.updateGroup(updatedGroup, connection);

        assertTrue(result, "El grupo debería actualizarse correctamente");
    }

    @Test
    void updateGroupFailsWhenGroupDoesNotExist() throws SQLException {
        GroupDTO group = new GroupDTO("123", "Updated Group", "User2", "Period2");

        boolean result = groupDAO.updateGroup(group, connection);

        assertFalse(result, "No debería permitir actualizar un grupo inexistente");
    }

    @Test
    void deleteGroupSuccessfully() throws SQLException {
        GroupDTO group = new GroupDTO("123", "Group A", "User1", "Period1");
        groupDAO.insertGroup(group, connection);

        boolean result = groupDAO.deleteGroup("123", connection);

        assertTrue(result, "El grupo debería eliminarse correctamente");
    }

    @Test
    void deleteGroupFailsWhenGroupDoesNotExist() throws SQLException {
        boolean result = groupDAO.deleteGroup("123", connection);

        assertFalse(result, "No debería permitir eliminar un grupo inexistente");
    }

    @Test
    void searchGroupByIdWhenGroupExists() throws SQLException {
        GroupDTO group = new GroupDTO("123", "Group A", "User1", "Period1");
        groupDAO.insertGroup(group, connection);

        GroupDTO result = groupDAO.searchGroupById("123", connection);

        assertNotNull(result, "El grupo no debería ser nulo");
        assertEquals("123", result.getNRC());
        assertEquals("Group A", result.getName());
        assertEquals("User1", result.getIdUser());
        assertEquals("Period1", result.getIdPeriod());
    }

    @Test
    void searchGroupByIdWhenGroupDoesNotExist() throws SQLException {
        GroupDTO result = groupDAO.searchGroupById("123", connection);

        assertNotNull(result, "El grupo no debería ser nulo");
        assertEquals("N/A", result.getNRC());
        assertEquals("N/A", result.getName());
        assertEquals("N/A", result.getIdUser());
        assertEquals("N/A", result.getIdPeriod());
    }

    @Test
    void getAllGroupsReturnsListOfGroups() throws SQLException {
        GroupDTO group1 = new GroupDTO("123", "Group A", "User1", "Period1");
        GroupDTO group2 = new GroupDTO("456", "Group B", "User2", "Period2");
        groupDAO.insertGroup(group1, connection);
        groupDAO.insertGroup(group2, connection);

        List<GroupDTO> result = groupDAO.getAllGroups(connection);

        assertNotNull(result, "La lista de grupos no debería ser nula");
        assertEquals(2, result.size(), "Debería haber 2 grupos en la lista");
    }

    @Test
    void getAllGroupsReturnsEmptyListWhenNoGroupsExist() throws SQLException {
        List<GroupDTO> result = groupDAO.getAllGroups(connection);

        assertNotNull(result, "La lista de grupos no debería ser nula");
        assertTrue(result.isEmpty(), "La lista de grupos debería estar vacía");
    }
}