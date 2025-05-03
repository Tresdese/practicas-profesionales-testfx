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
        connectionDB.closeConnection();
    }

    @BeforeEach
    void setUp() {
        studentDAO = new StudentDAO();
    }

    private String insertTestStudent(String tuiton, int state, String names, String surnames, String phone, String email, String user, String password, String nrc, String creditAdvance) throws SQLException {
        StudentDTO existingStudent = studentDAO.searchStudentByTuiton(tuiton);
        if (existingStudent != null) {
            return tuiton;
        }

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
            return tuiton;
        }
    }

    @Test
    void testInsertStudent() {
        try {
            StudentDTO student = new StudentDTO("12351", 1, "Juan", "Perez", "1234567890", "juan.perez@ejemplo.com", "juanperez_unique", "password", "11111", "50", 0.0);
            boolean result = studentDAO.insertStudent(student);
            assertTrue(result, "La inserción debería ser exitosa");
//TODO se ouede dividir esta prueba
            StudentDTO insertedStudent = studentDAO.searchStudentByTuiton("12351");
            assertNotNull(insertedStudent, "El estudiante debería existir en la base de datos");
            assertEquals("Juan", insertedStudent.getNames(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testSearchStudentByTuiton() {
        try {
            String tuiton = insertTestStudent("54331", 1, "Juana", "Lopez", "0987654321", "juana.lopez@ejemplo.com", "juanalopez_unique", "password123", "54321", "75");

            StudentDTO retrievedStudent = studentDAO.searchStudentByTuiton(tuiton);
            assertNotNull(retrievedStudent, "Debería encontrar el estudiante");
            assertEquals(tuiton, retrievedStudent.getTuiton(), "La matrícula debería coincidir");
            assertEquals("Juana", retrievedStudent.getNames(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testUpdateStudent() {
        try {
            String tuiton = insertTestStudent("67892", 1, "Original", "Estudiante", "1111111111", "original.estudiante@example.com", "originaluser_unique", "originalpass", "67890", "30");

            StudentDTO updatedStudent = new StudentDTO(tuiton, 1, "Actualizado", "Estudiante", "2222222222", "updated.estudiante@example.com", "updateduser_unique", "updatedpass", "54321", "60", 0.0);
            boolean updateResult = studentDAO.updateStudent(updatedStudent);
            assertTrue(updateResult, "La actualización debería ser exitosa");

            StudentDTO retrievedStudent = studentDAO.searchStudentByTuiton(tuiton);
            assertNotNull(retrievedStudent, "El estudiante debería existir");
            assertEquals("Actualizado", retrievedStudent.getNames(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testGetAllStudents() {
        try {
            insertTestStudent("11113", 1, "Test", "Student", "3333333333", "test.student@example.com", "testuser_unique", "testpass", "11111", "40");

            List<StudentDTO> students = studentDAO.getAllStudents();
            assertNotNull(students, "La lista no debería ser nula");
            assertFalse(students.isEmpty(), "La lista no debería estar vacía");

            boolean found = students.stream()
                    .anyMatch(s -> s.getTuiton().equals("11113"));
            assertTrue(found, "Nuestro estudiante de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteStudent() {
        try {
            String tuiton = insertTestStudent("22231", 1, "Delete", "Me", "4444444444", "delete.me@example.com", "deleteuser_unique", "deletepass", "11111", "20");

            StudentDTO before = studentDAO.searchStudentByTuiton(tuiton);
            assertNotNull(before, "El estudiante debería existir antes de eliminarlo");

            boolean deleted = studentDAO.deleteStudent(tuiton);
            assertTrue(deleted, "La eliminación debería ser exitosa");

            StudentDTO after = studentDAO.searchStudentByTuiton(tuiton);
            assertNull(after, "El estudiante no debería existir después de eliminarlo");
        } catch (SQLException e) {
            fail("Error: " + e.getMessage());
        }
    }
}