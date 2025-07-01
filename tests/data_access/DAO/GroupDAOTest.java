package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.GroupDAO;
import logic.DAO.PeriodDAO;
import logic.DAO.UserDAO;
import logic.DTO.GroupDTO;
import logic.DTO.PeriodDTO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupDAOTest {
    private Connection connection;
    private GroupDAO groupDAO;
    private PeriodDAO periodDAO;
    private UserDAO userDAO;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        ConnectionDataBase db = new ConnectionDataBase();
        connection = db.connectDataBase();
        groupDAO = new GroupDAO();
        periodDAO = new PeriodDAO();
        userDAO = new UserDAO();
        cleanTablesAndAutoincrement();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        cleanTablesAndAutoincrement();
        PeriodDTO periodo = new PeriodDTO("1", "Periodo Test", Timestamp.valueOf("2024-01-01 00:00:00"), Timestamp.valueOf("2024-12-31 00:00:00"));
        periodDAO.insertPeriod(periodo);

        UserDTO usuario = new UserDTO("1", 1, "1001", "Juan", "Pérez", "juanp", "password", Role.ACADEMIC);
        userDAO.insertUser(usuario);
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void cleanTablesAndAutoincrement() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM grupo");
        stmt.execute("DELETE FROM usuario");
        stmt.execute("DELETE FROM periodo");
        stmt.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        stmt.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        stmt.close();
    }

    @Test
    void insertGroupWhenGroupDoesNotExist() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Group A", "1", "1");
        boolean result = groupDAO.insertGroup(group);
        assertTrue(result, "El grupo debería insertarse correctamente");
    }

    @Test
    void insertGroupWhenGroupAlreadyExists() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("12344", "Group A", "1", "1");
        groupDAO.insertGroup(group);
        assertThrows(java.sql.SQLIntegrityConstraintViolationException.class, () -> {
            groupDAO.insertGroup(group);
        }, "No debería permitir insertar un grupo con el mismo NRC");
    }

    @Test
    void updateGroupSuccessfully() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Group A", "1", "1");
        groupDAO.insertGroup(group);
        GroupDTO updatedGroup = new GroupDTO("123", "Updated Group", "1", "1");
        boolean result = groupDAO.updateGroup(updatedGroup);
        assertTrue(result, "El grupo debería actualizarse correctamente");
    }

    @Test
    void updateGroupFailsWhenGroupDoesNotExist() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Updated Group", "1", "1");
        boolean result = groupDAO.updateGroup(group);
        assertFalse(result, "No debería permitir actualizar un grupo inexistente");
    }

    @Test
    void deleteGroupSuccessfully() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Group A", "1", "1");
        groupDAO.insertGroup(group);
        boolean result = groupDAO.deleteGroup("123");
        assertTrue(result, "El grupo debería eliminarse correctamente");
    }

    @Test
    void deleteGroupFailsWhenGroupDoesNotExist() throws SQLException, IOException {
        boolean result = groupDAO.deleteGroup("123");
        assertFalse(result, "No debería permitir eliminar un grupo inexistente");
    }

    @Test
    void searchGroupByIdWhenGroupExists() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Group A", "1", "1");
        groupDAO.insertGroup(group);
        GroupDTO result = groupDAO.searchGroupById("123");
        assertNotNull(result, "El grupo no debería ser nulo");
        assertEquals("123", result.getNRC());
        assertEquals("Group A", result.getName());
        assertEquals("1", result.getIdUser());
        assertEquals("1", result.getIdPeriod());
    }

    @Test
    void searchGroupByIdWhenGroupDoesNotExist() throws SQLException, IOException {
        GroupDTO result = groupDAO.searchGroupById("123");
        assertNotNull(result, "El grupo no debería ser nulo");
        assertEquals("N/A", result.getNRC());
        assertEquals("N/A", result.getName());
        assertEquals("N/A", result.getIdUser());
        assertEquals("N/A", result.getIdPeriod());
    }

    @Test
    void getAllGroupsReturnsListOfGroups() throws SQLException, IOException {
        GroupDTO group1 = new GroupDTO("123", "Group A", "1", "1");
        GroupDTO group2 = new GroupDTO("456", "Group B", "1", "1");
        groupDAO.insertGroup(group1);
        groupDAO.insertGroup(group2);
        List<GroupDTO> result = groupDAO.getAllGroups();
        assertNotNull(result, "La lista de grupos no debería ser nula");
        assertEquals(2, result.size(), "Debería haber 2 grupos en la lista");
    }

    @Test
    void getAllGroupsReturnsEmptyListWhenNoGroupsExist() throws SQLException, IOException {
        List<GroupDTO> result = groupDAO.getAllGroups();
        assertNotNull(result, "La lista de grupos no debería ser nula");
        assertTrue(result.isEmpty(), "La lista de grupos debería estar vacía");
    }
}