package logic.services;

import logic.DAO.StudentDAO;
import logic.DTO.StudentDTO;
import logic.exceptions.RepeatedEmail;
import logic.exceptions.RepeatedPhone;
import logic.exceptions.RepeatedTuiton;

import java.sql.Connection;
import java.sql.SQLException;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public void registerStudent(StudentDTO student, Connection connection) throws SQLException, RepeatedTuiton, RepeatedPhone, RepeatedEmail {
        if (studentDAO.isTuitonRegistered(student.getTuiton(), connection)) {
            throw new RepeatedTuiton("La matrícula ya está registrada.");
        }

        if (studentDAO.isPhoneRegistered(student.getPhone(), connection)) {
            throw new RepeatedPhone("El número de teléfono ya está registrado.");
        }

        if (studentDAO.isEmailRegistered(student.getEmail(), connection)) {
            throw new RepeatedEmail("El correo electrónico ya está registrado.");
        }

        boolean success = studentDAO.insertStudent(student, connection);
        if (!success) {
            throw new SQLException("No se pudo registrar el estudiante.");
        }
    }

    public void updateStudent(StudentDTO student, Connection connection) throws SQLException {
        boolean success = studentDAO.updateStudent(student, connection);
        if (!success) {
            throw new SQLException("No se pudo actualizar el estudiante.");
        }
    }
}