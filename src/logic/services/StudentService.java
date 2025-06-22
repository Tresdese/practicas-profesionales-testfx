package logic.services;

import logic.DAO.StudentDAO;
import logic.DTO.StudentDTO;
import logic.exceptions.RepeatedEmail;
import logic.exceptions.RepeatedPhone;
import logic.exceptions.RepeatedTuition;

import java.sql.SQLException;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public void registerStudent(StudentDTO student) throws SQLException, IOException, RepeatedTuition, RepeatedPhone, RepeatedEmail {
        if (studentDAO.isTuitonRegistered(student.getTuition())) {
            throw new RepeatedTuition("La matrícula ya está registrada.");
        }

        if (studentDAO.isPhoneRegistered(student.getPhone())) {
            throw new RepeatedPhone("El número de teléfono ya está registrado.");
        }

        if (studentDAO.isEmailRegistered(student.getEmail())) {
            throw new RepeatedEmail("El correo electrónico ya está registrado.");
        }

        boolean success = studentDAO.insertStudent(student);
        if (!success) {
            throw new SQLException("No se pudo registrar el estudiante.");
        }
    }

    public void updateStudent(StudentDTO student) throws SQLException, IOException, RepeatedTuition, RepeatedPhone, RepeatedEmail {
        boolean success = studentDAO.updateStudent(student);
        if (!success) {
            throw new SQLException("No se pudo actualizar el estudiante.");
        }
    }

    public void updateStudentStatus(String tuition, int state) throws SQLException {
        boolean success = studentDAO.updateStudentStatus(tuition, state);
        if (!success) {
            throw new SQLException("No se pudo eliminar el estudiante.");
        }
    }

    public List<StudentDTO> getAllStudents() throws SQLException, IOException {
        return studentDAO.getAllStudents();
    }

    public StudentDTO searchStudentByTuition(String tuition) throws SQLException, IOException {
        return studentDAO.searchStudentByTuition(tuition);
    }
}