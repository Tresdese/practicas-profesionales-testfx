package logic.interfaces;

import logic.DTO.DepartmentDTO;

import java.sql.SQLException;
import java.util.List;

public interface IDeparmentDAO {

    boolean insertDepartment(DepartmentDTO department) throws SQLException;

    boolean updateDepartment(DepartmentDTO department) throws SQLException;

    boolean deleteDepartment(int departmentId) throws SQLException;

    DepartmentDTO searchDepartmentById(int departmentId) throws SQLException;

    int getOrganizationIdByDepartmentId(int departmentId) throws SQLException;

    List<DepartmentDTO> getAllDepartmentsByOrganizationId(int organizationId) throws SQLException;

    List<DepartmentDTO> getAllDepartments() throws SQLException;

}
