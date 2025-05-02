package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.StudentDTO;

public interface IStudentDAO {

    boolean insertStudent(StudentDTO student) throws SQLException;

    boolean updateStudent(StudentDTO student) throws SQLException;

    boolean deleteStudent(String tuiton) throws SQLException;

    StudentDTO searchStudentByTuiton(String tuiton) throws SQLException;

    List<StudentDTO> getAllStudents() throws SQLException;
}
