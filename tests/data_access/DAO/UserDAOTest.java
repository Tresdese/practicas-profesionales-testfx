//package data_access.DAO;
//
//import data_access.ConecctionDataBase;
//import logic.DAO.UserDAO;
//import logic.DTO.Role;
//import logic.DTO.UserDTO;
//import logic.utils.PasswordHasher;
//import org.junit.jupiter.api.*;
//
//import java.sql.*;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class UserDAOTest {
//
//    private static ConecctionDataBase connectionDB;
//    private static Connection connection;
//    private UserDAO userDAO;
//    private int testUserId;
//
//    @BeforeAll
//    static void setUpClass() {
//        connectionDB = new ConecctionDataBase();
//        try {
//            connection = connectionDB.connectDB();
//        } catch (SQLException e) {
//            fail("Error al conectar a la base de datos: " + e.getMessage());
//        }
//    }
//
//    @AfterAll
//    static void tearDownClass() {
//        connectionDB.close();
//    }
//
//    @BeforeEach
//    void setUp() {
//        userDAO = new UserDAO(connection);
//    }
//
//    private int insertTestUser(String nombres, String apellidos,
//                               String nombreUsuario, String contraseña, String rol,
//                               int numeroPersonal) throws SQLException {
//        String sql = "INSERT INTO usuario (nombres, apellidos, nombreUsuario, contraseña, rol, numeroDePersonal) " +
//                "VALUES (?, ?, ?, ?, ?, ?)";
//        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
//            stmt.setString(1, nombres);
//            stmt.setString(2, apellidos);
//            stmt.setString(3, nombreUsuario);
//            stmt.setString(4, PasswordHasher.hashPassword(contraseña));
//            stmt.setString(5, rol);
//            stmt.setInt(6, numeroPersonal);
//            stmt.executeUpdate();
//
//            try (ResultSet rs = stmt.getGeneratedKeys()) {
//                if (rs.next()) {
//                    return rs.getInt(1);
//                } else {
//                    throw new SQLException("No se generó ID para el usuario");
//                }
//            }
//        }
//    }
//
//    @Test
//    void testInsertUser() {
//        try {
//            int randomNum = (int) (Math.random() * 1000);
//            String nombreUsuario = "user" + randomNum;
//            int numeroPersonal = randomNum + 1000;
//
//            UserDTO user = new UserDTO(
//                    "0",
//                    String.valueOf(numeroPersonal),
//                    "Nombre Test",
//                    "Apellido Test",
//                    nombreUsuario,
//                    "password123",
//                    Role.COORDINADOR
//            );
//
//            boolean result = userDAO.insertUser(user);
//            assertTrue(result, "La inserción debería ser exitosa");
//
//            String sql = "SELECT idUsuario, numeroDePersonal, contraseña FROM usuario WHERE nombreUsuario = ?";
//            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//                stmt.setString(1, nombreUsuario);
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next(), "Debería encontrar al usuario insertado");
//                    int insertedId = rs.getInt("idUsuario");
//                    int insertedNumPersonal = rs.getInt("numeroDePersonal");
//                    String hashedPassword = rs.getString("contraseña");
//
//                    UserDTO retrievedUser = userDAO.searchUserById(String.valueOf(insertedId));
//                    assertNotNull(retrievedUser, "El usuario debería existir en la base de datos");
//                    assertEquals(nombreUsuario, retrievedUser.getUserName(), "El nombre de usuario debería coincidir");
//                    assertEquals(String.valueOf(numeroPersonal), retrievedUser.getStaffNumber(), "El número de personal debería coincidir");
//                    assertTrue(PasswordHasher.verifyPassword("password123", hashedPassword), "La contraseña debería estar correctamente cifrada");
//                }
//            }
//        } catch (SQLException e) {
//            fail("Error en testInsertUser: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testGetUser() {
//        try {
//            String uniqueUsername = "juanperez" + UUID.randomUUID().toString().substring(0, 5);
//            int numeroPersonal = (int) (Math.random() * 1000) + 2000;
//
//            testUserId = insertTestUser(
//                    "Juan",
//                    "Pérez",
//                    uniqueUsername,
//                    "clave123",
//                    "COORDINADOR",
//                    numeroPersonal
//            );
//
//            UserDTO user = userDAO.searchUserById(String.valueOf(testUserId));
//            assertNotNull(user, "Debería encontrar al usuario");
//            assertEquals("Juan", user.getNames(), "El nombre debería coincidir");
//            assertEquals("Pérez", user.getSurnames(), "El apellido debería coincidir");
//            assertEquals(String.valueOf(numeroPersonal), user.getStaffNumber(), "El número de personal debería coincidir");
//        } catch (SQLException e) {
//            fail("Error en testGetUser: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testUpdateUser() {
//        try {
//            String username = "marialopez" + UUID.randomUUID().toString().substring(0, 5);
//            int numeroPersonal = (int) (Math.random() * 1000) + 3000;
//
//            testUserId = insertTestUser(
//                    "María",
//                    "López",
//                    username,
//                    "clave456",
//                    "ACADEMICO",
//                    numeroPersonal
//            );
//
//            UserDTO currentUser = userDAO.searchUserById(String.valueOf(testUserId));
//            int nuevoNumeroPersonal = numeroPersonal + 1;
//
//            UserDTO userToUpdate = new UserDTO(
//                    String.valueOf(testUserId),
//                    String.valueOf(nuevoNumeroPersonal),
//                    "María Actualizada",
//                    "López Actualizada",
//                    username,
//                    "nuevaclave789",
//                    Role.ACADEMICO_EVALUADOR
//            );
//
//            boolean updateResult = userDAO.updateUser(userToUpdate);
//            assertTrue(updateResult, "La actualización debería ser exitosa");
//
//            String sql = "SELECT contraseña FROM usuario WHERE idUsuario = ?";
//            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//                stmt.setInt(1, testUserId);
//                try (ResultSet rs = stmt.executeQuery()) {
//                    assertTrue(rs.next(), "Debería encontrar el usuario actualizado");
//                    String hashedPassword = rs.getString("contraseña");
//                    assertTrue(PasswordHasher.verifyPassword("nuevaclave789", hashedPassword),
//                            "La contraseña actualizada debería estar correctamente cifrada");
//                }
//            }
//
//            UserDTO updatedUser = userDAO.searchUserById(String.valueOf(testUserId));
//            assertNotNull(updatedUser, "El usuario debería existir después de actualizar");
//            assertEquals("María Actualizada", updatedUser.getNames(), "El nombre debería actualizarse");
//            assertEquals(String.valueOf(nuevoNumeroPersonal), updatedUser.getStaffNumber(), "El número de personal debería actualizarse");
//        } catch (SQLException e) {
//            fail("Error en testUpdateUser: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testDeleteUserFunctionality() {
//        try {
//            String username1 = "profesor" + UUID.randomUUID().toString().substring(0, 5);
//            String username2 = "evaluador" + UUID.randomUUID().toString().substring(0, 5);
//            String username3 = "coordinador" + UUID.randomUUID().toString().substring(0, 5);
//
//            int numeroPersonal1 = 7010;
//            int numeroPersonal2 = 7011;
//            int numeroPersonal3 = 7012;
//
//            int id1 = insertTestUser(
//                    "Profesor",
//                    "García",
//                    username1,
//                    "clave123",
//                    "ACADEMICO",
//                    numeroPersonal1
//            );
//
//            int id2 = insertTestUser(
//                    "Evaluador",
//                    "Pérez",
//                    username2,
//                    "clave456",
//                    "ACADEMICO_EVALUADOR",
//                    numeroPersonal2
//            );
//
//            int id3 = insertTestUser(
//                    "Coordinador",
//                    "López",
//                    username3,
//                    "clave789",
//                    "COORDINADOR",
//                    numeroPersonal3
//            );
//
//            UserDTO user1 = userDAO.searchUserById(String.valueOf(id1));
//            UserDTO user2 = userDAO.searchUserById(String.valueOf(id2));
//            UserDTO user3 = userDAO.searchUserById(String.valueOf(id3));
//
//            assertNotNull(user1, "El profesor debería existir");
//            assertNotNull(user2, "El evaluador debería existir");
//            assertNotNull(user3, "El coordinador debería existir");
//
//            UserDTO userToDelete = new UserDTO(
//                    String.valueOf(id2),
//                    String.valueOf(numeroPersonal2),
//                    "Evaluador",
//                    "Pérez",
//                    username2,
//                    "clave456",
//                    Role.ACADEMICO_EVALUADOR
//            );
//
//            boolean deleteResult = userDAO.deleteUser(userToDelete.getIdUser());
//            assertTrue(deleteResult, "La eliminación debería ser exitosa");
//
//            UserDTO deletedUser = userDAO.searchUserById(String.valueOf(id2));
//            assertNull(deletedUser, "El evaluador eliminado no debería existir");
//
//            UserDTO remainingUser1 = userDAO.searchUserById(String.valueOf(id1));
//            UserDTO remainingUser3 = userDAO.searchUserById(String.valueOf(id3));
//
//            assertNotNull(remainingUser1, "El profesor debería seguir existiendo");
//            assertNotNull(remainingUser3, "El coordinador debería seguir existiendo");
//
//            assertEquals(username1, remainingUser1.getUserName(), "El nombre de usuario del profesor debería permanecer igual");
//            assertEquals(username3, remainingUser3.getUserName(), "El nombre de usuario del coordinador debería permanecer igual");
//            assertEquals(String.valueOf(numeroPersonal1), remainingUser1.getStaffNumber(), "El número de personal del profesor debería permanecer igual");
//            assertEquals(String.valueOf(numeroPersonal3), remainingUser3.getStaffNumber(), "El número de personal del coordinador debería permanecer igual");
//
//            List<UserDTO> allRemainingUsers = userDAO.getAllUsers();
//            assertTrue(allRemainingUsers.size() >= 2, "Deberían quedar al menos los dos usuarios no eliminados");
//
//        } catch (SQLException e) {
//            fail("Error en testDeleteUserFunctionality: " + e.getMessage());
//        }
//    }
//}