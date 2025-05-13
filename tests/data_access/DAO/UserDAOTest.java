package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.utils.PasswordHasher;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private UserDAO userDAO;

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
        connectionDB.close();
    }

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO(connection);
        try {
            connection.prepareStatement("DELETE FROM usuario").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla usuario: " + e.getMessage());
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
            assertTrue(result, "La inserción debería ser exitosa");

            UserDTO insertedUser = userDAO.searchUserById("1");
            assertNotNull(insertedUser, "El usuario debería existir en la base de datos");
            assertEquals("Juan", insertedUser.getNames(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertUser: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserById() {
        try {
            insertTestUser("2", "54321", "María", "López", "marialopez", "clave123", Role.ACADEMICO);

            UserDTO retrievedUser = userDAO.searchUserById("2");
            assertNotNull(retrievedUser, "El usuario debería existir en la base de datos");
            assertEquals("María", retrievedUser.getNames(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchUserById: " + e.getMessage());
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
            assertTrue(result, "La actualización debería ser exitosa");

            UserDTO retrievedUser = userDAO.searchUserById("3");
            assertNotNull(retrievedUser, "El usuario debería existir después de actualizar");
            assertEquals("Carlos Actualizado", retrievedUser.getNames(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateUser: " + e.getMessage());
        }
    }

    @Test
    void testDeleteUser() {
        try {
            insertTestUser("4", "11223", "Ana", "Martínez", "anamartinez", "clave789", Role.COORDINADOR);

            boolean result = userDAO.deleteUser("4");
            assertTrue(result, "La eliminación debería ser exitosa");

            UserDTO deletedUser = userDAO.searchUserById("4");
            assertNull(deletedUser, "El usuario eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteUser: " + e.getMessage());
        }
    }

    @Test
    void testGetAllUsers() {
        try {
            insertTestUser("5", "33445", "Luis", "Hernández", "luishernandez", "clave123", Role.ACADEMICO);
            insertTestUser("6", "55667", "Sofía", "Ramírez", "sofiaramirez", "clave456", Role.COORDINADOR);

            List<UserDTO> users = userDAO.getAllUsers();
            assertNotNull(users, "La lista de usuarios no debería ser nula");
            assertTrue(users.size() >= 2, "Deberían existir al menos dos usuarios en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllUsers: " + e.getMessage());
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
}