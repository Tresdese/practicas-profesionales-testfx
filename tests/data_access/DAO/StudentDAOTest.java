package data_access.DAO;

import data_access.ConnectionDataBase;
import logic.DAO.StudentDAO;
import logic.DTO.StudentDTO;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentDAOTest {

    private static ConnectionDataBase connectionDB;
    private static Connection connection;
    private StudentDAO studentDAO;
    private static final int TEST_PERIODO_ID = 1001;
    private static final int TEST_NRC = 11111;

    @BeforeAll
    static void setUpClass() {
        try {
            connectionDB = new ConnectionDataBase();
            connection = connectionDB.connectDataBase();
        } catch (SQLException e) {
            fail("Error al conectar a la base de datos: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado al conectar a la base de datos: " + e.getMessage());
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
            connection.prepareStatement("DELETE FROM grupo").executeUpdate();
            connection.prepareStatement("DELETE FROM periodo").executeUpdate();

            String sqlPeriodo = "INSERT INTO periodo (idPeriodo, nombre, fechaInicio, fechaFin) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlPeriodo)) {
                preparedStatement.setInt(1, TEST_PERIODO_ID);
                preparedStatement.setString(2, "Periodo Test");
                preparedStatement.setDate(3, Date.valueOf("2024-01-01"));
                preparedStatement.setDate(4, Date.valueOf("2024-12-31"));
                preparedStatement.executeUpdate();
            }

            String sqlGrupo = "INSERT INTO grupo (NRC, nombre, idUsuario, idPeriodo) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlGrupo)) {
                preparedStatement.setInt(1, TEST_NRC);
                preparedStatement.setString(2, "Grupo Test");
                preparedStatement.setNull(3, java.sql.Types.INTEGER);
                preparedStatement.setInt(4, TEST_PERIODO_ID);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            fail("Error al preparar datos de prueba: " + e.getMessage());
        }
    }

    @Test
    void testInsertStudent() {
        try {
            StudentDTO student = new StudentDTO("S12345442", 1, "Juan", "Perez", "1234567890", "juan.perez@example.com", "juanperez", "password", String.valueOf(TEST_NRC), "50", 0.0);
            boolean result = studentDAO.insertStudent(student);
            assertTrue(result, "La inserción debería ser exitosa");

            StudentDTO insertedStudent = studentDAO.searchStudentByTuition("S12345442");
            assertNotNull(insertedStudent, "El estudiante debería existir en la base de datos");
            assertEquals("Juan", insertedStudent.getNames(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testInsertStudent: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testInsertStudent: " + e.getMessage());
        }
    }

    @Test
    void testSearchStudentByTuition() {
        try {
            insertTestStudent("S54321768", 1, "Juana", "Lopez", "0987654321", "juana.lopez@example.com", "juanalopez", "password123", String.valueOf(TEST_NRC), "75");

            StudentDTO retrievedStudent = studentDAO.searchStudentByTuition("S54321768");
            assertNotNull(retrievedStudent, "Debería encontrar el estudiante");
            assertEquals("Juana", retrievedStudent.getNames(), "El nombre debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchStudentByTuition: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testSearchStudentByTuition: " + e.getMessage());
        }
    }

    @Test
    void testUpdateStudent() {
        try {
            insertTestStudent("S67890755", 1, "Original", "Estudiante", "1111111111", "original@example.com", "originaluser", "originalpass", String.valueOf(TEST_NRC), "30");

            StudentDTO updatedStudent = new StudentDTO("S67890755", 1, "Actualizado", "Estudiante", "2222222222", "updated@example.com", "updateduser", "updatedpass", String.valueOf(TEST_NRC), "60", 0.0);
            boolean result = studentDAO.updateStudent(updatedStudent);
            assertTrue(result, "La actualización debería ser exitosa");

            StudentDTO retrievedStudent = studentDAO.searchStudentByTuition("S67890755");
            assertNotNull(retrievedStudent, "El estudiante debería existir después de actualizar");
            assertEquals("Actualizado", retrievedStudent.getNames(), "El nombre debería actualizarse");
        } catch (SQLException e) {
            fail("Error en testUpdateStudent: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testUpdateStudent: " + e.getMessage());
        }
    }

    @Test
    void testDeleteStudent() {
        try {
            insertTestStudent("S22222174", 1, "Eliminar", "Estudiante", "4444444444", "delete@example.com", "deleteuser", "deletepass", String.valueOf(TEST_NRC), "20");

            boolean result = studentDAO.deleteStudent("S22222174");
            assertTrue(result, "La eliminación debería ser exitosa");

            StudentDTO deletedStudent = studentDAO.searchStudentByTuition("S22222174");
            assertEquals("N/A", deletedStudent.getTuition(), "El estudiante eliminado no debería existir");
        } catch (SQLException e) {
            fail("Error en testDeleteStudent: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testDeleteStudent: " + e.getMessage());
        }
    }

    @Test
    void testGetAllStudents() {
        try {
            insertTestStudent("S33333798", 1, "Test", "Student", "5555555555", "test@example.com", "testuser", "testpass", String.valueOf(TEST_NRC), "40");

            List<StudentDTO> students = studentDAO.getAllStudents();
            assertNotNull(students, "La lista no debería ser nula");
            assertFalse(students.isEmpty(), "La lista no debería estar vacía");

            boolean found = students.stream().anyMatch(student -> student.getTuition().equals("S33333798"));
            assertTrue(found, "El estudiante de prueba debería estar en la lista");
        } catch (SQLException e) {
            fail("Error en testGetAllStudents: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testGetAllStudents: " + e.getMessage());
        }
    }

    private void insertTestStudent(String tuition, int state, String names, String surnames, String phone, String email, String user, String password, String nrc, String creditAdvance) throws SQLException {
        String sql = "INSERT INTO estudiante (matricula, estado, nombres, apellidos, telefono, correo, usuario, contraseña, NRC, avanceCrediticio, calificacionFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tuition);
            preparedStatement.setInt(2, state);
            preparedStatement.setString(3, names);
            preparedStatement.setString(4, surnames);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, email);
            preparedStatement.setString(7, user);
            preparedStatement.setString(8, password);
            preparedStatement.setString(9, nrc);
            preparedStatement.setString(10, creditAdvance);
            preparedStatement.setDouble(11, 0.0);
            preparedStatement.executeUpdate();
        }
    }

    @Test
    void testUpdateStudentState() {
        try {
            insertTestStudent("S88888888", 1, "Estado", "Prueba", "9999999999", "estado@prueba.com", "estadouser", "estadopass", String.valueOf(TEST_NRC), "10");
            boolean updated = studentDAO.updateStudentStatus("S88888888", 2);
            assertTrue(updated, "El estado debería actualizarse correctamente");

            StudentDTO student = studentDAO.searchStudentByTuition("S88888888");
            assertEquals(2, student.getState(), "El estado del estudiante debería ser 2");
        } catch (SQLException e) {
            fail("Error en testUpdateStudentState: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testUpdateStudentState: " + e.getMessage());
        }
    }

    @Test
    void testSearchStudentByUserAndPassword() {
        try {
            insertTestStudent("S77777777", 1, "Usuario", "Clave", "8888888888", "usuario@clave.com", "usuarioprueba", "claveprueba", String.valueOf(TEST_NRC), "15");
            StudentDTO student = studentDAO.searchStudentByUserAndPassword("usuarioprueba", "claveprueba");
            assertNotNull(student, "El estudiante debería encontrarse");
            assertEquals("S77777777", student.getTuition(), "La matrícula debería coincidir");
        } catch (SQLException e) {
            fail("Error en testSearchStudentByUserAndPassword: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testSearchStudentByUserAndPassword: " + e.getMessage());
        }
    }

    @Test
    void testIsTuitonRegistered() {
        try {
            insertTestStudent("S99999999", 1, "Matricula", "Registrada", "7777777777", "matricula@reg.com", "matriculareg", "matriculapass", String.valueOf(TEST_NRC), "20");
            assertTrue(studentDAO.isTuitonRegistered("S99999999"), "La matrícula debería estar registrada");
            assertFalse(studentDAO.isTuitonRegistered("NO_EXISTE"), "La matrícula no debería estar registrada");
        } catch (SQLException e) {
            fail("Error en testIsTuitonRegistered: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testIsTuitonRegistered: " + e.getMessage());
        }
    }

    @Test
    void testIsPhoneRegistered() {
        try {
            insertTestStudent("S66666666", 1, "Telefono", "Registrado", "6666666666", "telefono@reg.com", "telefonoreg", "telefonopass", String.valueOf(TEST_NRC), "25");
            assertTrue(studentDAO.isPhoneRegistered("6666666666"), "El teléfono debería estar registrado");
            assertFalse(studentDAO.isPhoneRegistered("0000000000"), "El teléfono no debería estar registrado");
        } catch (SQLException e) {
            fail("Error en testIsPhoneRegistered: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testIsPhoneRegistered: " + e.getMessage());
        }
    }

    @Test
    void testIsEmailRegistered() {
        try {
            insertTestStudent("S55555555", 1, "Correo", "Registrado", "5555555555", "correo@reg.com", "correoreg", "correopass", String.valueOf(TEST_NRC), "30");
            assertTrue(studentDAO.isEmailRegistered("correo@reg.com"), "El correo debería estar registrado");
            assertFalse(studentDAO.isEmailRegistered("noexiste@correo.com"), "El correo no debería estar registrado");
        } catch (SQLException e) {
            fail("Error en testIsEmailRegistered: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testIsEmailRegistered: " + e.getMessage());
        }
    }

    @Test
    void testInsertStudentDuplicateTuition() {
        try {
            insertTestStudent("S11111111", 1, "Duplicado", "Test", "1111111111", "dup@test.com", "dupuser", "duppass", String.valueOf(TEST_NRC), "10");
            StudentDTO duplicate = new StudentDTO("S11111111", 1, "Duplicado2", "Test2", "2222222222", "dup2@test.com", "dupuser2", "duppass2", String.valueOf(TEST_NRC), "20", 0.0);
            assertThrows(SQLException.class, () -> studentDAO.insertStudent(duplicate), "No debe permitir insertar matrícula duplicada");
        } catch (SQLException e) {
            fail("Error en testInsertStudentDuplicateTuition: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testInsertStudentDuplicateTuition: " + e.getMessage());
        }
    }

    @Test
    void testUpdateStudentNonExistent() {
        try {
            StudentDTO nonExistent = new StudentDTO("S00000000", 1, "No", "Existe", "0000000000", "no@existe.com", "nouser", "nopass", String.valueOf(TEST_NRC), "0", 0.0);
            boolean result = studentDAO.updateStudent(nonExistent);
            assertFalse(result, "No debe actualizar un estudiante inexistente");
        } catch (SQLException e) {
            fail("Error en testUpdateStudentNonExistent: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testUpdateStudentNonExistent: " + e.getMessage());
        }
    }

    @Test
    void testDeleteStudentNonExistent() {
        try {
            boolean result = studentDAO.deleteStudent("S00000001");
            assertFalse(result, "No debe eliminar un estudiante inexistente");
        } catch (SQLException e) {
            fail("Error en testDeleteStudentNonExistent: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testDeleteStudentNonExistent: " + e.getMessage());
        }
    }

    @Test
    void testSearchStudentByUserAndPasswordInvalidCredentials() {
        try {
            insertTestStudent("S22222222", 1, "Usuario", "Clave", "2222222222", "user@clave.com", "userprueba", "claveprueba", String.valueOf(TEST_NRC), "10");
            StudentDTO student = studentDAO.searchStudentByUserAndPassword("userprueba", "clave_incorrecta");
            assertEquals("N/A", student.getTuition(), "Debe devolver estudiante no encontrado");
        } catch (SQLException e) {
            fail("Error en testSearchStudentByUserAndPasswordInvalidCredentials: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testSearchStudentByUserAndPasswordInvalidCredentials: " + e.getMessage());
        }
    }

    @Test
    void testGetAllStudentsEmpty() {
        try {
            connection.prepareStatement("DELETE FROM estudiante").executeUpdate();
            List<StudentDTO> students = studentDAO.getAllStudents();
            assertNotNull(students, "La lista no debe ser nula");
            assertTrue(students.isEmpty(), "La lista debe estar vacía si no hay estudiantes");
        } catch (SQLException e) {
            fail("Error en testGetAllStudentsEmpty: " + e.getMessage());
        } catch (IOException e) {
            fail("Error al cargar la configuración de la base de datos: " + e.getMessage());
        } catch (Exception e) {
            fail("Error inesperado en testGetAllStudentsEmpty: " + e.getMessage());
        }
    }

    @Test
    void testInsertStudentWithNullFields() {
        StudentDTO student = new StudentDTO(null, 1, null, null, null, null, null, null, String.valueOf(TEST_NRC), null, 0.0);
        assertThrows(SQLException.class, () -> studentDAO.insertStudent(student), "No debe permitir insertar campos nulos obligatorios");
    }
}