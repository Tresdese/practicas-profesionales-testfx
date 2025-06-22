package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.StudentProjectDTO;

public interface IStudentProjectDAO {
    boolean insertStudentProject(StudentProjectDTO studentProject) throws SQLException, IOException;

    boolean updateStudentProject(StudentProjectDTO studentProject) throws SQLException, IOException;

    boolean deleteStudentProject(StudentProjectDTO studentProject) throws SQLException, IOException;

    StudentProjectDTO searchStudentProjectByIdProject(String idProject) throws SQLException, IOException;

    StudentProjectDTO searchStudentProjectByIdTuiton(String tuiton) throws SQLException, IOException;

    List<StudentProjectDTO> getAllStudentProjects() throws SQLException, IOException;
}
