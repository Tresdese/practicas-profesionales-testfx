package logic.interfaces;

import logic.DTO.DepartmentDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IDeparmentDAO {

    boolean insertDepartment(DepartmentDTO department) throws SQLException, IOException;

    boolean updateDepartment(DepartmentDTO department) throws SQLException, IOException;

    boolean updateDepartmentStatus(int departmentId, int status) throws SQLException, IOException;

    boolean deleteDepartment(int departmentId) throws SQLException, IOException;

    DepartmentDTO searchDepartmentById(int departmentId) throws SQLException, IOException;

    int getOrganizationIdByDepartmentId(int departmentId) throws SQLException, IOException;

    List<DepartmentDTO> getAllDepartmentsByOrganizationId(int organizationId) throws SQLException, IOException;

    List<DepartmentDTO> getAllDepartments() throws SQLException, IOException;

}
