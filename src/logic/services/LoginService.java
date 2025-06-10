package logic.services;

import data_access.ConnectionDataBase;
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

    public LoginService() throws SQLException {
        Connection connection = new ConnectionDataBase().connectDB();
        this.studentDAO = new StudentDAO();
        this.userDAO = new UserDAO();
    }

    public Object login(String username, String plainPassword) throws SQLException, InvalidCredential {
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);

        StudentDTO student = studentDAO.searchStudentByUserAndPassword(username, hashedPassword);
        if (student != null && !"N/A".equals(student.getTuiton())) {
            return student;
        }

        UserDTO user = userDAO.searchUserByUsernameAndPassword(username, hashedPassword);
        if (user != null && !"INVALID".equals(user.getIdUser())) {
            return user;
        }

        throw new InvalidCredential("Credenciales inválidas");
    }
}