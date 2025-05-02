package logic.services;

import data_access.ConecctionDataBase;
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
        Connection connection = new ConecctionDataBase().connectDB();
        this.studentDAO = new StudentDAO(connection);
        this.userDAO = new UserDAO(connection);
    }

    public Object login(String username, String plainPassword) throws SQLException, InvalidCredential {
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);

        // Buscar estudiante por usuario y contraseña cifrada
        StudentDTO student = studentDAO.searchStudentByUserAndPassword(username, hashedPassword);
        if (!"N/A".equals(student.getTuiton())) {
            return student;
        }

        // Buscar usuario general por usuario y contraseña cifrada
        UserDTO user = userDAO.searchUserByUsernameAndPassword(username, hashedPassword);
        if (!"INVALID".equals(user.getIdUser())) {
            return user;
        }

        throw new InvalidCredential("Credenciales inválidas");
    }
}