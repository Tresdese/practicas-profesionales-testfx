package logic.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import logic.DTO.StudentDTO;

public interface IStudentDAO {

    boolean insertStudent(StudentDTO student) throws SQLException, IOException;

    boolean updateStudent(StudentDTO student) throws SQLException, IOException;

    boolean deleteStudent(String tuiton) throws SQLException, IOException;

    StudentDTO searchStudentByTuition(String tuiton) throws SQLException, IOException;

    List<StudentDTO> getAllStudents() throws SQLException, IOException;

    StudentDTO searchStudentByUserAndPassword(String username, String password) throws SQLException, IOException;

    boolean isTuitonRegistered(String tuiton) throws SQLException, IOException;

    boolean isPhoneRegistered(String phone) throws SQLException, IOException;

    boolean isEmailRegistered(String email) throws SQLException, IOException;
}
