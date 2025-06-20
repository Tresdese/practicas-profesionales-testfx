package logic.interfaces;

import java.sql.SQLException;
import java.util.List;
import logic.DTO.StudentDTO;

public interface IStudentDAO {

    boolean insertStudent(StudentDTO student) throws SQLException;

    boolean updateStudent(StudentDTO student) throws SQLException;

    boolean deleteStudent(String tuiton) throws SQLException;

    StudentDTO searchStudentByTuition(String tuiton) throws SQLException;

    List<StudentDTO> getAllStudents() throws SQLException;

    StudentDTO searchStudentByUserAndPassword(String username, String password) throws SQLException;

    boolean isTuitonRegistered(String tuiton) throws SQLException;

    boolean isPhoneRegistered(String phone) throws SQLException;

    boolean isEmailRegistered(String email) throws SQLException;
}
