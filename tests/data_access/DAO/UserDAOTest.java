package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.utils.PasswordHasher;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private static ConnectionDataBase connectionDB;
    private static Connection connection;
    private UserDAO userDAO;

    @BeforeAll
    static void setUpAll() {
        connectionDB = new ConnectionDataBase();
        try {
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error connecting to the database: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        connectionDB.close();
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        try {
            connection.prepareStatement("DELETE FROM usuario").executeUpdate();
        } catch (SQLException e) {
            fail("Error cleaning the usuario table: " + e.getMessage());
        }
    }

    @Test
    void testInsertUser() {
        try {
            UserDTO user = new UserDTO(
                    "1",
                    "12345",
                    "Juan",
                    "Pérez",
                    "juanperez",
                    "password123",
                    Role.COORDINADOR
            );

            boolean result = userDAO.insertUser(user);
            assertTrue(result, "Insertion should be successful");

            UserDTO insertedUser = userDAO.searchUserById("1");
            assertNotNull(insertedUser, "User should exist in the database");
            assertEquals("Juan", insertedUser.getNames(), "Name should match");
        } catch (SQLException e) {
            fail("Error in testInsertUser: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserById() {
        try {
            insertTestUser("2", "54321", "María", "López", "marialopez", "clave123", Role.ACADEMICO);

            UserDTO retrievedUser = userDAO.searchUserById("2");
            assertNotNull(retrievedUser, "User should exist in the database");
            assertEquals("María", retrievedUser.getNames(), "Name should match");
        } catch (SQLException e) {
            fail("Error in testSearchUserById: " + e.getMessage());
        }
    }

    @Test
    void testUpdateUser() {
        try {
            insertTestUser("3", "67890", "Carlos", "Gómez", "carlosgomez", "clave456", Role.ACADEMICO);

            UserDTO updatedUser = new UserDTO(
                    "3",
                    "67891",
                    "Carlos Actualizado",
                    "Gómez Actualizado",
                    "carlosgomez",
                    "nuevaclave789",
                    Role.ACADEMICO_EVALUADOR
            );

            boolean result = userDAO.updateUser(updatedUser);
            assertTrue(result, "Update should be successful");

            UserDTO retrievedUser = userDAO.searchUserById("3");
            assertNotNull(retrievedUser, "User should exist after update");
            assertEquals("Carlos Actualizado", retrievedUser.getNames(), "Name should be updated");
        } catch (SQLException e) {
            fail("Error in testUpdateUser: " + e.getMessage());
        }
    }

    @Test
    void testDeleteUser() {
        try {
            insertTestUser("4", "11223", "Ana", "Martínez", "anamartinez", "clave789", Role.COORDINADOR);

            boolean result = userDAO.deleteUser("4");
            assertTrue(result, "Deletion should be successful");

            UserDTO deletedUser = userDAO.searchUserById("4");
            assertNull(deletedUser, "Deleted user should not exist");
        } catch (SQLException e) {
            fail("Error in testDeleteUser: " + e.getMessage());
        }
    }

    @Test
    void testGetAllUsers() {
        try {
            insertTestUser("5", "33445", "Luis", "Hernández", "luishernandez", "clave123", Role.ACADEMICO);
            insertTestUser("6", "55667", "Sofía", "Ramírez", "sofiaramirez", "clave456", Role.COORDINADOR);

            List<UserDTO> users = userDAO.getAllUsers();
            assertNotNull(users, "User list should not be null");
            assertTrue(users.size() >= 2, "There should be at least two users in the list");
        } catch (SQLException e) {
            fail("Error in testGetAllUsers: " + e.getMessage());
        }
    }

    private void insertTestUser(String id, String staffNumber, String names, String surnames, String username, String password, Role role) throws SQLException {
        String sql = "INSERT INTO usuario (idUsuario, numeroDePersonal, nombres, apellidos, nombreUsuario, contraseña, rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, staffNumber);
            stmt.setString(3, names);
            stmt.setString(4, surnames);
            stmt.setString(5, username);
            stmt.setString(6, PasswordHasher.hashPassword(password));
            stmt.setString(7, role.toString());
            stmt.executeUpdate();
        }
    }

    @Test
    void testInsertUser_Duplicate() {
        try {
            UserDTO user = new UserDTO(
                    "10", "99999", "Duplicado", "Usuario", "duplicado", "clave", Role.ACADEMICO
            );
            assertTrue(userDAO.insertUser(user), "First insert should be successful");
            assertThrows(SQLException.class, () -> userDAO.insertUser(user), "Should not allow duplicate user id");
        } catch (SQLException e) {
            fail("Error in testInsertUser_Duplicate: " + e.getMessage());
        }
    }

    @Test
    void testUpdateUser_NotExists() {
        try {
            UserDTO user = new UserDTO(
                    "999", "00000", "No", "Existe", "noexiste", "clave", Role.GUEST
            );
            boolean result = userDAO.updateUser(user);
            assertFalse(result, "Should not update a non-existent user");
        } catch (SQLException e) {
            fail("Error in testUpdateUser_NotExists: " + e.getMessage());
        }
    }

    @Test
    void testDeleteUser_NotExists() {
        try {
            boolean result = userDAO.deleteUser("888");
            assertFalse(result, "Should not delete a non-existent user");
        } catch (SQLException e) {
            fail("Error in testDeleteUser_NotExists: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserById_NotExists() {
        try {
            UserDTO user = userDAO.searchUserById("777");
            assertNull(user, "Searching for a non-existent user should return null");
        } catch (SQLException e) {
            fail("Error in testSearchUserById_NotExists: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserByUsernameAndPassword() {
        try {
            String password = "claveSegura";
            insertTestUser("20", "11111", "Nombre", "Apellido", "usuario20", password, Role.ACADEMICO);
            UserDTO user = userDAO.searchUserByUsernameAndPassword("usuario20", PasswordHasher.hashPassword(password));
            assertNotNull(user, "User should be found");
            assertEquals("Nombre", user.getNames(), "Name should match");
        } catch (SQLException e) {
            fail("Error in testSearchUserByUsernameAndPassword: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserByUsernameAndPassword_Invalid() {
        try {
            UserDTO user = userDAO.searchUserByUsernameAndPassword("noexiste", "contraseñaIncorrecta");
            assertEquals("INVALID", user.getIdUser(), "Should return invalid user");
            assertEquals(Role.GUEST, user.getRole(), "Role should be GUEST");
        } catch (SQLException e) {
            fail("Error in testSearchUserByUsernameAndPassword_Invalid: " + e.getMessage());
        }
    }

    @Test
    void testIsUserRegistered() {
        try {
            insertTestUser("30", "22222", "Existente", "Usuario", "usuario30", "clave", Role.COORDINADOR);
            assertTrue(userDAO.isUserRegistered("30"), "Should detect existing user");
            assertFalse(userDAO.isUserRegistered("noexiste"), "Should not detect non-existent user");
        } catch (SQLException e) {
            fail("Error in testIsUserRegistered: " + e.getMessage());
        }
    }

    @Test
    void testIsNameRegistered() {
        try {
            insertTestUser("40", "33333", "Nombre", "Apellido", "usuario40", "clave", Role.ACADEMICO);
            assertTrue(userDAO.isNameRegistered("usuario40"), "Should detect existing username");
            assertFalse(userDAO.isNameRegistered("noexiste"), "Should not detect non-existent username");
        } catch (SQLException e) {
            fail("Error in testIsNameRegistered: " + e.getMessage());
        }
    }

    @Test
    void testGetUserIdByUsername() {
        try {
            insertTestUser("50", "44444", "Nombre", "Apellido", "usuario50", "clave", Role.ACADEMICO);
            String id = userDAO.getUserIdByUsername("usuario50");
            assertEquals("50", id, "Should return the correct id");
        } catch (SQLException e) {
            fail("Error in testGetUserIdByUsername: " + e.getMessage());
        }
    }

    @Test
    void testGetAllUsers_EmptyTable() {
        try {
            connection.prepareStatement("DELETE FROM usuario").executeUpdate();
            List<UserDTO> users = userDAO.getAllUsers();
            assertNotNull(users, "User list should not be null");
            assertEquals(0, users.size(), "List should be empty if there are no users");
        } catch (SQLException e) {
            fail("Error in testGetAllUsers_EmptyTable: " + e.getMessage());
        }
    }

    @Test
    void testInsertUser_DuplicateUsername() {
        try {
            UserDTO user1 = new UserDTO("60", "55555", "Nombre", "Apellido", "usuario60", "clave", Role.ACADEMICO);
            UserDTO user2 = new UserDTO("61", "55556", "Nombre2", "Apellido2", "usuario60", "clave2", Role.COORDINADOR);
            assertTrue(userDAO.insertUser(user1), "First insert should be successful");
            assertThrows(SQLException.class, () -> userDAO.insertUser(user2), "Should not allow duplicate username");
        } catch (SQLException e) {
            fail("Error in testInsertUser_DuplicateUsername: " + e.getMessage());
        }
    }

    @Test
    void testGetAllUsers_MultipleRecords() {
        try {
            insertTestUser("80", "77777", "Nombre1", "Apellido1", "usuario80", "clave1", Role.ACADEMICO);
            insertTestUser("81", "88888", "Nombre2", "Apellido2", "usuario81", "clave2", Role.COORDINADOR);
            insertTestUser("82", "99999", "Nombre3", "Apellido3", "usuario82", "clave3", Role.ACADEMICO_EVALUADOR);
            List<UserDTO> users = userDAO.getAllUsers();
            assertTrue(users.size() >= 3, "There should be at least three users in the list");
        } catch (SQLException e) {
            fail("Error in testGetAllUsers_MultipleRecords: " + e.getMessage());
        }
    }
}