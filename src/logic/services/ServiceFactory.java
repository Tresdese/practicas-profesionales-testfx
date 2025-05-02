package logic.services;

import data_access.ConecctionDataBase;

import java.sql.Connection;

public class ServiceFactory {

    private static StudentService studentService;

    public static StudentService getStudentService() {
        if (studentService == null) {
            try {
                Connection connection = new ConecctionDataBase().connectDB();
                studentService = new StudentService(connection);
            } catch (Exception e) {
                throw new RuntimeException("Error al inicializar StudentService: " + e.getMessage(), e);
            }
        }
        return studentService;
    }
}