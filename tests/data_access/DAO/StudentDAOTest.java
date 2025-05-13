package data_access.DAO;

import data_access.ConecctionDataBase;
import logic.DAO.StudentDAO;
import logic.DTO.StudentDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentDAOTest {

    private static ConecctionDataBase connectionDB;
    private static Connection connection;
    private StudentDAO studentDAO;

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
        studentDAO = new StudentDAO();
        try {
            connection.prepareStatement("DELETE FROM estudiante").executeUpdate();
        } catch (SQLException e) {
            fail("Error al limpiar la tabla estudiante: " + e.getMessage());
        }
    }

    @Test
    void testInsertStudent() {
        try {
            StudentDTO student = new StudentDTO("S12345442", 1, "Juan", "Perez", "1234567890", "juan.perez@example.com", "juanperez", "password", "11111", "50", 0.0);
            boolean result = studentDAO.insertStudent(student);
            assertTrue(result, "La inserción debería ser exitosa");

            StudentDTO insertedStudent = studentDAO.searchStudentByTuiton("S12345442");
            assertNotNull(insertedStudent, "El estudiante debería existir en la base de datos");
            assertEquals("Juan", insertedStudent.getNames(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertStudent: " + e.getMessage());
        }
    }

    @Test
    void testSearchStudentByTuiton() {
        try {
            insertTestStudent("S54321768", 1, "Juana", "Lopez", "0987654321", "juana.lopez@example.com", "juanalopez", "password123", "54321", "75");

            StudentDTO retrievedStudent = studentDAO.searchStudentByTuiton("S54321768");
            assertNotNull(retrievedStudent, "Debería encontrar el estudiante");
            assertEquals("Juana", retrievedStudent.getNames(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchStudentByTuiton: " + e.getMessage());
        }
    }

    @Test
    void testUpdateStudent() {
        try {
            insertTestStudent("S67890755", 1, "Original", "Estudiante", "1111111111", "original@example.com", "originaluser", "originalpass", "67890", "30");

            StudentDTO updatedStudent = new StudentDTO("S67890755", 1, "Actualizado", "Estudiante", "2222222222", "updated@example.com", "updateduser", "updatedpass", "54321", "60", 0.0);
            boolean result = studentDAO.updateStudent(updatedStudent);
            assertTrue(result, "La actualización debería ser exitosa");

            StudentDTO retrievedStudent = studentDAO.searchStudentByTuiton("S67890755");
            assertNotNull(retrievedStudent, "El estudiante debería existir después de actualizar");
            assertEquals("Actualizado", retrievedStudent.getNames(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateStudent: " + e.getMessage());
        }
    }

    @Test
    void testDeleteStudent() {
        try {
            insertTestStudent("S22222174", 1, "Eliminar", "Estudiante", "4444444444", "delete@example.com", "deleteuser", "deletepass", "11111", "20");

            boolean result = studentDAO.deleteStudent("S22222174");
            assertTrue(result, "La eliminación debería ser exitosa");

            StudentDTO deletedStudent = studentDAO.searchStudentByTuiton("22222");
            assertEquals("N/A", deletedStudent.getTuiton(), "El estudiante eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteStudent: " + e.getMessage());
        }
    }

    @Test
    void testGetAllStudents() {
        try {
            insertTestStudent("S33333798", 1, "Test", "Student", "5555555555", "test@example.com", "testuser", "testpass", "11111", "40");

            List<StudentDTO> students = studentDAO.getAllStudents();
            assertNotNull(students, "La lista no debería ser nula");
            assertFalse(students.isEmpty(), "La lista no debería estar vacía");

            boolean found = students.stream().anyMatch(s -> s.getTuiton().equals("S33333798"));
            assertTrue(found, "El estudiante de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllStudents: " + e.getMessage());
        }
    }

    private void insertTestStudent(String tuiton, int state, String names, String surnames, String phone, String email, String user, String password, String nrc, String creditAdvance) throws SQLException {
        String sql = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tuiton);
            stmt.setInt(2, state);
            stmt.setString(3, names);
            stmt.setString(4, surnames);
            stmt.setString(5, phone);
            stmt.setString(6, email);
            stmt.setString(7, user);
            stmt.setString(8, password);
            stmt.setString(9, nrc);
            stmt.setString(10, creditAdvance);
            stmt.executeUpdate();
        }
    }
}