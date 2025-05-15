package logic.interfaces;

import java.sql.SQLException;
import java.util.List;
import logic.DTO.StudentProjectDTO;

public interface IStudentProjectDAO {
    boolean insertStudentProject(StudentProjectDTO studentProject) throws SQLException;

    boolean updateStudentProject(StudentProjectDTO studentProject) throws SQLException;

    boolean deleteStudentProject(StudentProjectDTO studentProject) throws SQLException;

    StudentProjectDTO searchStudentProjectByIdProject(String idProject) throws SQLException;

    List<StudentProjectDTO> getAllStudentProjects() throws SQLException;
}
