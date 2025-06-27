package logic.services;

import data_access.ConnectionDataBase;

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

    public static UserService getUserService() {
        if (userService == null) {
            try {
                Connection connection = new ConnectionDataBase().connectDB();
                userService = new UserService();
            } catch (Exception e) {
                throw new RuntimeException("Error al inicializar UserService: " + e.getMessage(), e);
            }
        }
        return userService;
    }
}