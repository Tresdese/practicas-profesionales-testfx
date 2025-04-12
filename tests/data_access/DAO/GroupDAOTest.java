package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.GroupDAO;
import logic.DTO.GroupDTO;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GroupDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private GroupDAO groupDAO;

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
        groupDAO = new GroupDAO();
    }

    private String insertTestGroup(String nrc, String name, String idUser, String idPeriod) throws SQLException {
        GroupDTO existingGroup = groupDAO.getGroup(nrc, connection);
        if (existingGroup != null) {
            return nrc;
        }

        String sql = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nrc);
            stmt.setString(2, name);
            stmt.setString(3, idUser);
            stmt.setString(4, idPeriod);
            stmt.executeUpdate();
            return nrc;
        }
    }

    @Test
    void testInsertGroup() {
        try {
            String nrc = "12345";
            String name = "Grupo de Prueba";
            String idUser = "1";
            String idPeriod = "222601"; // Actualizado a 222601

            GroupDTO group = new GroupDTO(nrc, name, idUser, idPeriod);
            boolean result = groupDAO.insertGroup(group, connection);
            assertTrue(result, "La inserción debería ser exitosa");

            GroupDTO insertedGroup = groupDAO.getGroup(nrc, connection);
            assertNotNull(insertedGroup, "El grupo debería existir en la base de datos");
            assertEquals(name, insertedGroup.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetGroup() {
        try {
            String nrc = insertTestGroup("54321", "Grupo para Consulta", "2", "222601"); // Actualizado a 222601

            GroupDTO retrievedGroup = groupDAO.getGroup(nrc, connection);
            assertNotNull(retrievedGroup, "Debería encontrar el grupo");
            assertEquals(nrc, retrievedGroup.getNRC(), "El NRC debería coincidir");
            assertEquals("Grupo para Consulta", retrievedGroup.getName(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testUpdateGroup() {
        try {
            String nrc = insertTestGroup("67890", "Grupo Original", "3", "222601"); // Actualizado a 222601

            GroupDTO group = new GroupDTO(nrc, "Grupo Actualizado", "5", "222601"); // Actualizado a 222601
            boolean updateResult = groupDAO.updateGroup(group, connection);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            GroupDTO updatedGroup = groupDAO.getGroup(nrc, connection);
            assertNotNull(updatedGroup, "El grupo debería existir");
            assertEquals("Grupo Actualizado", updatedGroup.getName(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetAllGroups() {
        try {
            insertTestGroup("11111", "Grupo para Listar", "6", "222601"); // Actualizado a 222601

            List<GroupDTO> groups = groupDAO.getAllGroups(connection);
            assertNotNull(groups, "La lista no debería ser nula");
            assertFalse(groups.isEmpty(), "La lista no debería estar vacía");

            boolean found = groups.stream()
                    .anyMatch(g -> g.getNRC().equals("11111"));
            assertTrue(found, "Nuestro grupo de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteGroup() {
        try {
            String nrc = insertTestGroup("22222", "Grupo para Eliminar", "7", "222601"); // Actualizado a 222601

            GroupDTO before = groupDAO.getGroup(nrc, connection);
            assertNotNull(before, "El grupo debería existir antes de eliminarlo");

            boolean deleted = groupDAO.deleteGroup(nrc, connection);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            GroupDTO after = groupDAO.getGroup(nrc, connection);
            assertNull(after, "El grupo no debería existir después de eliminarlo");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }
}