package logic.services;

import data_access.ConnectionDataBase;
import logic.DAO.StudentDAO;
import logic.DAO.UserDAO;
import logic.DTO.StudentDTO;
import logic.DTO.UserDTO;
import logic.exceptions.InactiveUser;
import logic.exceptions.InvalidCredential;
import logic.utils.PasswordHasher;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginService {
    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    public LoginService() throws SQLException {
        this.studentDAO = new StudentDAO();
        this.userDAO = new UserDAO();
    }

    public Object login(String username, String plainPassword) throws SQLException, IOException, InvalidCredential, InactiveUser {
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);

        StudentDTO student = studentDAO.searchStudentByUserAndPassword(username, hashedPassword);
        if (student != null && !"N/A".equals(student.getTuition())) {
            if (student.getState() == 1) {
                return student;
            } else if (student.getState() == 0) {
                throw new InactiveUser("Estudiante inactivo o dado de baja.");
            }
        }

        UserDTO user = userDAO.searchUserByUsernameAndPassword(username, hashedPassword);
        if (user != null && !"INVALID".equals(user.getIdUser())) {
            if (user.getStatus() == 1) {
                return user;
            } else if (user.getStatus() == 0) {
                throw new InactiveUser("Usuario inactivo o dado de baja.");
            }
        }

        throw new InvalidCredential("Credenciales inválidas");
    }
}