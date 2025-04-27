package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.StudentProjectDTO;

public interface IStudentProjectDAO {
    boolean insertStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException;

    boolean updateStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException;

    boolean deleteStudentProject(StudentProjectDTO studentProject, Connection connection) throws SQLException;

    StudentProjectDTO searchStudentProjectByIdProject(String idProject, Connection connection) throws SQLException;

    List<StudentProjectDTO> getAllStudentProjects(Connection connection) throws SQLException;
}
