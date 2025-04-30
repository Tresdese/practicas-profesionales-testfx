package logic.services;

import logic.DAO.StudentDAO;
import logic.DAO.UserDAO;
import logic.DTO.StudentDTO;
import logic.DTO.UserDTO;
import logic.exceptions.InvalidCredential;
import logic.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginService {
    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    public LoginService() {
        this.studentDAO = new StudentDAO();
        this.userDAO = new UserDAO();
    }

    public Object login(String username, String plainPassword, Connection connection) throws SQLException, InvalidCredential {
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);

        // Buscar estudiante por usuario y contraseña cifrada
        StudentDTO student = studentDAO.searchStudentByUserAndPassword(username, hashedPassword, connection);
        if (!"N/A".equals(student.getTuiton())) {
            return student;
        }

        // Buscar usuario general por usuario y contraseña cifrada
        UserDTO user = userDAO.searchUserByUsernameAndPassword(username, hashedPassword, connection);
        if (!"INVALID".equals(user.getIdUser())) {
            return user;
        }

        throw new InvalidCredential("Credenciales inválidas");
    }
}