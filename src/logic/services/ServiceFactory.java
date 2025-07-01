package logic.services;

import data_access.ConnectionDataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ServiceFactory {

    private static StudentService studentService;

    public static StudentService getStudentService() {
        if (studentService == null) {
            try {
                studentService = new StudentService();
            } catch (Exception e) {
                throw new RuntimeException("Error al inicializar StudentService: " + e.getMessage(), e);
            }
        }
        return studentService;
    }

    private static UserService userService;

    public static UserService getUserService() throws SQLException, IOException {
        if (userService == null) {
            Connection connection = new ConnectionDataBase().connectDataBase();
            userService = new UserService();
        }
        return userService;
    }
}