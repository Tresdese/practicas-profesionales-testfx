package logic.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.StudentDTO;

public interface IStudentDAO {
    boolean insertStudent(StudentDTO student, Connection connection) throws SQLException;

    boolean updateStudent(StudentDTO student, Connection connection) throws SQLException;

    boolean deleteStudent(StudentDTO student, Connection connection) throws SQLException;

    StudentDTO getStudent(String tuiton, Connection connection) throws SQLException;

    List<StudentDTO> getAllStudents(Connection connection) throws SQLException;
}
