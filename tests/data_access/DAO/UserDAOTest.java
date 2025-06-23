package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.UserDAO;
import logic.DTO.Role;
import logic.DTO.UserDTO;
import logic.utils.PasswordHasher;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private static ConnectionDataBase connectionDB;
    private static Connection connection;
    private UserDAO userDAO;

    @BeforeAll
    static void setUpAll() {
        try {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDB();
        } catch (SQLException e) {
            fail("Error de base de datos conectando a la base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuracion de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
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
            fail("Error de base de datos limpiando la tabla de usuario: " + e.getMessage());
        }
    }

    @Test
    void testInsertUser() {
        try {
            UserDTO user = new UserDTO(
                    "1",
                    1,
                    "12345",
                    "Juan",
                    "Pérez",
                    "juanperez",
                    "password123",
                    Role.COORDINADOR
            );

            boolean result = userDAO.insertUser(user);
            assertTrue(result, "La inserción del usuario debe ser exitosa");

            UserDTO insertedUser = userDAO.searchUserById("1");
            assertNotNull(insertedUser, "Usuario debe existir en la base de datos");
            assertEquals("Juan", insertedUser.getNames(), "Nombre debe coincidir");
        } catch (SQLException e) {
            fail("Error en la base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserById() {
        try {
            UserDTO user = new UserDTO(
                    "2", 1, "54321", "María", "López", "marialopez",
                    PasswordHasher.hashPassword("clave123"), Role.ACADEMICO
            );
            assertTrue(userDAO.insertUser(user));
            UserDTO retrievedUser = userDAO.searchUserById("2");
            assertNotNull(retrievedUser, "Usuario debe existir en la base de datos");
            assertEquals("María", retrievedUser.getNames(), "Nombre debe coincidir");
        } catch (SQLException e) {
            fail("Error de base de datos" + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testUpdateUser() {
        try {
            UserDTO user = new UserDTO(
                    "3", 1, "12345", "Carlos", "Gómez", "carlosgomez",
                    PasswordHasher.hashPassword("clave123"), Role.ACADEMICO
            );
            assertTrue(userDAO.insertUser(user));

            UserDTO updatedUser = new UserDTO(
                    "3", 1, "12345", "Carlos", "Gómez", "carlosgomez",
                    PasswordHasher.hashPassword("nuevaclave789"), Role.ACADEMICO_EVALUADOR
            );
            assertTrue(userDAO.updateUser(updatedUser), "Actualizacion del usuario debe ser exitosa");

            UserDTO retrievedUser = userDAO.searchUserById("3");
            assertEquals("carlosgomez", retrievedUser.getUserName());
            assertEquals(Role.ACADEMICO_EVALUADOR, retrievedUser.getRole());
        } catch (SQLException e) {
            fail("Error de base de datos en testUpdateUser: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testDeleteUser() {
        UserDAO userDAO = new UserDAO();
        String idUser = "1001"; // id numérico como String
        UserDTO user = new UserDTO(
                idUser, 1, "99999", "Test", "Delete", "deleteuser", "password", Role.ACADEMICO_EVALUADOR
        );
        try {
            userDAO.insertUser(user);

            boolean deleted = userDAO.deleteUser(idUser);
            assertTrue(deleted, "El usuario debe eliminarse correctamente");
        } catch (Exception e) {
            fail("Excepción inesperada en testDeleteUser: " + e.getMessage());
        }
    }

    @Test
    void testGetAllUsers() {
        try {
            UserDTO user = new UserDTO(
                    "2", 1, "54321", "María", "López", "marialopez1",
                    PasswordHasher.hashPassword("clave123"), Role.ACADEMICO
            );
            assertTrue(userDAO.insertUser(user));
            UserDTO user2 = new UserDTO(
                    "22", 1, "54322", "María", "López", "marialopez2",
                    PasswordHasher.hashPassword("clave123"), Role.ACADEMICO
            );
            assertTrue(userDAO.insertUser(user2));
            List<UserDTO> users = userDAO.getAllUsers();
            assertNotNull(users, "Lista de usuarios no debe ser nula");
            assertTrue(users.size() >= 2, "No debe ser menor a 2 usuarios en la lista");
        } catch (SQLException e) {
            fail("Error de base de datos en testGetAllUsers: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testInsertUserDuplicate() {
        try {
            UserDTO user = new UserDTO(
                    "10", 1, "99999", "Duplicado", "Usuario", "duplicado", "clave", Role.ACADEMICO
            );
            assertTrue(userDAO.insertUser(user), "Primer insert debe ser exitoso");
            assertThrows(SQLException.class, () -> userDAO.insertUser(user), "No debería permitir insertar un usuario duplicado");
        } catch (SQLException e) {
            fail("Error en testInsertDuplicate de base de datos " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testUpdateUserNotExists() {
        try {
            UserDTO user = new UserDTO(
                    "999", 1, "00000", "No", "Existe", "noexiste", "clave", Role.GUEST
            );
            boolean result = userDAO.updateUser(user);
            assertFalse(result, "No debería actualizar un usuario que no existe");
        } catch (SQLException e) {
            fail("Error de base de datos en testUpdateUserNotExists" + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testDeleteUserNotExists() {
        try {
            boolean result = userDAO.deleteUser("888");
            assertFalse(result, "No debería eliminar un usuario que no existe");
        } catch (SQLException e) {
            fail("Error en testDeleteUserNotExists de base de datos : " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserByIdNotExists() {
        try {
            UserDTO user = userDAO.searchUserById("99999");
            assertNotNull(user, "El método nunca retorna null");
            assertEquals("INVALID", user.getIdUser());
            assertEquals(Role.GUEST, user.getRole());
        } catch (SQLException e) {
            fail("Error en testSearchUserByIdNotExists: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserByUsernameAndPassword() {
        try {
            UserDAO userDAO = new UserDAO();
            String idUser = "1002"; // id numérico como String
            String username = "usuario_test_search";
            String password = "clave123";
            String hashedPassword = PasswordHasher.hashPassword(password);

            UserDTO user = new UserDTO(
                    idUser, 1, "12345", "Nombre", "Apellido", username,
                    hashedPassword, Role.ACADEMICO
            );
            userDAO.insertUser(user);

            UserDTO foundUser = userDAO.searchUserByUsernameAndPassword(username, hashedPassword);
            assertEquals("Nombre", foundUser.getNames(), "Nombre debe coincidir");
            assertEquals(username, foundUser.getUserName(), "El nombre de usuario debe coincidir");
        } catch (Exception e) {
            fail("Error en testSearchUserByUsernameAndPassword: " + e.getMessage());
        }
    }

    @Test
    void testSearchUserByUsernameAndPasswordInvalid() {
        try {
            UserDTO user = userDAO.searchUserByUsernameAndPassword("noexiste", "contraseñaIncorrecta");
            assertEquals("INVALID", user.getIdUser(), "Debe retornar un usuario inválido");
            assertEquals(Role.GUEST, user.getRole(), "Role debe ser GUEST para usuario no encontrado");
        } catch (SQLException e) {
            fail("Error en testSearchUserByUsernameAndPasswordInvalid de base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testIsUserRegistered() {
        UserDAO userDAO = new UserDAO();
        String idUser = "12345"; // id numérico como String
        UserDTO user = new UserDTO(
                idUser,
                1,
                "12345",
                "Test",
                "User",
                "testuser123",
                "password",
                Role.COORDINADOR
        );
        try {
            userDAO.insertUser(user);
            assertTrue(userDAO.isUserRegistered(idUser), "Deberia detectar usuario existente");
        } catch (Exception e) {
            fail("Error en testIsUserRegistered: " + e.getMessage());
        } finally {
            try {
                userDAO.deleteUser(idUser);
            } catch (Exception e) {
                fail("Error al limpiar el usuario de prueba: " + e.getMessage());
            }
        }
    }

    @Test
    void testIsNameRegistered() {
        try {
            UserDTO user = new UserDTO(
                    "31", 1, "54322", "María", "López", "marialopez_test",
                    PasswordHasher.hashPassword("clave123"), Role.ACADEMICO
            );
            assertTrue(userDAO.insertUser(user));
            assertTrue(userDAO.isNameRegistered("marialopez_test"), "Deberia detectar nombre de usuario existente");
            assertFalse(userDAO.isNameRegistered("noexiste"), "No debería detectar nombre de usuario no existente");
        } catch (SQLException e) {
            fail("Error en testIsNameRegistered de base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testGetUserIdByUsername() {
        try {
            UserDAO userDAO = new UserDAO();
            String idUser = "1050"; // id numérico como String
            String username = "marialopez_testid";
            UserDTO user = new UserDTO(
                    idUser, 1, "54321", "María", "López", username,
                    PasswordHasher.hashPassword("clave123"), Role.ACADEMICO
            );
            userDAO.insertUser(user);

            String resultId = userDAO.getUserIdByUsername(username);
            assertEquals(idUser, resultId, "Deberia retornar el ID correcto del usuario");
        } catch (SQLException e) {
            fail("Error en testGetUserIdByUsername de base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error de IO en testGetUserIdByUsername: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testGetUserIdByUsername: " + e.getMessage());
        }
    }

    @Test
    void testGetAllUsersEmptyTable() {
        try {
            connection.prepareStatement("DELETE FROM usuario").executeUpdate();
            List<UserDTO> users = userDAO.getAllUsers();
            assertNotNull(users, "Lista de usuarios no debe ser nula");
            assertEquals(0, users.size(), "Lista de usuarios debe estar vacía");
        } catch (SQLException e) {
            fail("Error en testGetAllUsersEmptyTable de base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testInsertUserDuplicateUsername() {
        try {
            UserDTO user1 = new UserDTO("60", 1, "55555", "Nombre", "Apellido", "usuario60", "clave", Role.ACADEMICO);
            UserDTO user2 = new UserDTO("61", 1, "55556", "Nombre2", "Apellido2", "usuario60", "clave2", Role.COORDINADOR);
            assertTrue(userDAO.insertUser(user1), "Primer insert debe ser exitoso");
            assertThrows(SQLException.class, () -> userDAO.insertUser(user2), "No debería permitir insertar un usuario con nombre de usuario duplicado");
        } catch (SQLException e) {
            fail("Error en testInsertUserDuplicateUsername de base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error cargando la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    void testGetAllUsersMultipleRecords() {
        try {
            UserDAO userDAO = new UserDAO();
            for (int i = 0; i < 3; i++) {
                UserDTO user = new UserDTO(
                        String.valueOf(100 + i),
                        1,
                        String.valueOf(1000 + i),
                        "Maria",
                        "Lopez",
                        "marialopez" + i,
                        "password" + i,
                        Role.ACADEMICO
                );
                userDAO.insertUser(user);
            }

            List<UserDTO> users = userDAO.getAllUsers();
            long count = users.stream().filter(u -> u.getUserName().startsWith("marialopez")).count();
            Assertions.assertEquals(3, count);

        } catch (Exception e) {
            Assertions.fail("Error in testGetAllUsersMultipleRecords: " + e.getMessage());
        }
    }
}