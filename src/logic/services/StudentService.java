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

    public StudentService(Connection connection) {
        this.studentDAO = new StudentDAO(connection);
    }

    public void registerStudent(StudentDTO student) throws SQLException, RepeatedTuiton, RepeatedPhone, RepeatedEmail {
        if (studentDAO.isTuitonRegistered(student.getTuiton())) {
            throw new RepeatedTuiton("La matrícula ya está registrada.");
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

    public void updateStudent(StudentDTO student) throws SQLException {
        boolean success = studentDAO.updateStudent(student);
        if (!success) {
            throw new SQLException("No se pudo actualizar el estudiante.");
        }
    }
}