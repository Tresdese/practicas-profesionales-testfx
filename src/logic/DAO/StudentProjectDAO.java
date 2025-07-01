package logic.DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.StudentProjectDTO;
import logic.interfaces.IStudentProjectDAO;

public class StudentProjectDAO implements IStudentProjectDAO {
    private final static String SQL_INSERT = "INSERT INTO proyecto_estudiante (idProyecto, matricula) VALUES (?, ?)";
    private final static String SQL_UPDATE = "UPDATE proyecto_estudiante SET matricula = ? WHERE idProyecto = ?";
    private final static String SQL_UPDATE_PROJECT = "UPDATE proyecto_estudiante SET idProyecto = ? WHERE matricula = ?";
    private final static String SQL_DELETE = "DELETE FROM proyecto_estudiante WHERE idProyecto = ?";
    private final static String SQL_SELECT = "SELECT * FROM proyecto_estudiante WHERE idProyecto = ?";
    private final static String SQL_SELECT_PROJECT_BY_TUITION = "SELECT * FROM proyecto_estudiante WHERE matricula = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM proyecto_estudiante";

    public boolean insertStudentProject(StudentProjectDTO studentProject) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, studentProject.getIdProject());
            statement.setString(2, studentProject.getTuition());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStudentProject(StudentProjectDTO studentProject) throws SQLException, IOException {
        if (studentProject.getIdProject() == null) {
            throw new SQLException("idProyecto no puede ser nulo");
        }
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_PROJECT)) {
            statement.setString(1, studentProject.getIdProject());
            statement.setString(2, studentProject.getTuition());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteStudentProject(StudentProjectDTO studentProject) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setString(1, studentProject.getIdProject());
            return statement.executeUpdate() > 0;
        }
    }

    public StudentProjectDTO searchStudentProjectByIdProject(String idProject) throws SQLException, IOException {
        StudentProjectDTO studentProject = new StudentProjectDTO("N/A", "N/A");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setString(1, idProject);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    studentProject = new StudentProjectDTO(resultSet.getString("idProyecto"), resultSet.getString("matricula"));
                }
            }
        }
        return studentProject;
    }

public StudentProjectDTO searchStudentProjectByIdTuiton(String tuiton) throws SQLException, IOException {
        StudentProjectDTO studentProject = new StudentProjectDTO("N/A", "N/A");
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_PROJECT_BY_TUITION)) {
            statement.setString(1, tuiton);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    studentProject = new StudentProjectDTO(resultSet.getString("idProyecto"), resultSet.getString("matricula"));
                }
            }
        }
        return studentProject;
    }

    public List<StudentProjectDTO> getAllStudentProjects() throws SQLException, IOException {
        List<StudentProjectDTO> studentProjects = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                studentProjects.add(new StudentProjectDTO(resultSet.getString("idProyecto"), resultSet.getString("matricula")));
            }
        }
        return studentProjects;
    }
}