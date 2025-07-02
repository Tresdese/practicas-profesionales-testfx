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

    private Connection databaseConnection;
    private GroupDAO groupDataAccessObject;
    private PeriodDAO periodDataAccessObject;
    private UserDAO userDataAccessObject;

    @BeforeAll
    void setUpAll() throws SQLException, IOException {
        ConnectionDataBase connectionDataBase = new ConnectionDataBase();
        databaseConnection = connectionDataBase.connectDataBase();
        groupDataAccessObject = new GroupDAO();
        periodDataAccessObject = new PeriodDAO();
        userDataAccessObject = new UserDAO();
        clearTablesAndResetAutoIncrement();
    }

    @BeforeEach
    void setUp() throws SQLException, IOException {
        clearTablesAndResetAutoIncrement();
        PeriodDTO period = new PeriodDTO("1", "Periodo Test", Timestamp.valueOf("2024-01-01 00:00:00"), Timestamp.valueOf("2024-12-31 00:00:00"));
        periodDataAccessObject.insertPeriod(period);

        UserDTO user = new UserDTO("1", 1, "1001", "Juan", "Pérez", "juanp", "password", Role.ACADEMIC);
        userDataAccessObject.insertUser(user);
    }

    @AfterAll
    void tearDownAll() throws SQLException, IOException {
        if (databaseConnection != null && !databaseConnection.isClosed()) {
            databaseConnection.close();
        }
    }

    private void clearTablesAndResetAutoIncrement() throws SQLException {
        Statement statement = databaseConnection.createStatement();
        statement.execute("DELETE FROM grupo");
        statement.execute("DELETE FROM usuario");
        statement.execute("DELETE FROM periodo");
        statement.execute("ALTER TABLE grupo AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE usuario AUTO_INCREMENT = 1");
        statement.execute("ALTER TABLE periodo AUTO_INCREMENT = 1");
        statement.close();
    }

    @Test
    void insertGroupWhenGroupDoesNotExist() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Group A", "1", "1");
        boolean wasInserted = groupDataAccessObject.insertGroup(group);
        assertTrue(wasInserted, "El grupo debería insertarse correctamente");
    }

    @Test
    void insertGroupWhenGroupAlreadyExists() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("12344", "Group A", "1", "1");
        groupDataAccessObject.insertGroup(group);
        assertThrows(java.sql.SQLIntegrityConstraintViolationException.class, () -> {
            groupDataAccessObject.insertGroup(group);
        }, "No debería permitir insertar un grupo con el mismo NRC");
    }

    @Test
    void updateGroupSuccessfully() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Group A", "1", "1");
        groupDataAccessObject.insertGroup(group);
        GroupDTO updatedGroup = new GroupDTO("123", "Updated Group", "1", "1");
        boolean wasUpdated = groupDataAccessObject.updateGroup(updatedGroup);
        assertTrue(wasUpdated, "El grupo debería actualizarse correctamente");
    }

    @Test
    void updateGroupFailsWhenGroupDoesNotExist() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Updated Group", "1", "1");
        boolean wasUpdated = groupDataAccessObject.updateGroup(group);
        assertFalse(wasUpdated, "No debería permitir actualizar un grupo inexistente");
    }

    @Test
    void deleteGroupSuccessfully() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Group A", "1", "1");
        groupDataAccessObject.insertGroup(group);
        boolean wasDeleted = groupDataAccessObject.deleteGroup("123");
        assertTrue(wasDeleted, "El grupo debería eliminarse correctamente");
    }

    @Test
    void deleteGroupFailsWhenGroupDoesNotExist() throws SQLException, IOException {
        boolean wasDeleted = groupDataAccessObject.deleteGroup("123");
        assertFalse(wasDeleted, "No debería permitir eliminar un grupo inexistente");
    }

    @Test
    void searchGroupByIdWhenGroupExists() throws SQLException, IOException {
        GroupDTO group = new GroupDTO("123", "Group A", "1", "1");
        groupDataAccessObject.insertGroup(group);
        GroupDTO foundGroup = groupDataAccessObject.searchGroupById("123");
        assertNotNull(foundGroup, "El grupo no debería ser nulo");
        assertEquals("123", foundGroup.getNRC());
        assertEquals("Group A", foundGroup.getName());
        assertEquals("1", foundGroup.getIdUser());
        assertEquals("1", foundGroup.getIdPeriod());
    }

    @Test
    void searchGroupByIdWhenGroupDoesNotExist() throws SQLException, IOException {
        GroupDTO foundGroup = groupDataAccessObject.searchGroupById("123");
        assertNotNull(foundGroup, "El grupo no debería ser nulo");
        assertEquals("N/A", foundGroup.getNRC());
        assertEquals("N/A", foundGroup.getName());
        assertEquals("N/A", foundGroup.getIdUser());
        assertEquals("N/A", foundGroup.getIdPeriod());
    }

    @Test
    void getAllGroupsReturnsListOfGroups() throws SQLException, IOException {
        GroupDTO groupOne = new GroupDTO("123", "Group A", "1", "1");
        GroupDTO groupTwo = new GroupDTO("456", "Group B", "1", "1");
        groupDataAccessObject.insertGroup(groupOne);
        groupDataAccessObject.insertGroup(groupTwo);
        List<GroupDTO> groupList = groupDataAccessObject.getAllGroups();
        assertNotNull(groupList, "La lista de grupos no debería ser nula");
        assertEquals(2, groupList.size(), "Debería haber 2 grupos en la lista");
    }

    @Test
    void getAllGroupsReturnsEmptyListWhenNoGroupsExist() throws SQLException, IOException {
        List<GroupDTO> groupList = groupDataAccessObject.getAllGroups();
        assertNotNull(groupList, "La lista de grupos no debería ser nula");
        assertTrue(groupList.isEmpty(), "La lista de grupos debería estar vacía");
    }
}