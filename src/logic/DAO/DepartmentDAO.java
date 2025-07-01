package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data_access.ConnectionDataBase;
import logic.DTO.DepartmentDTO;
import logic.interfaces.IDeparmentDAO;

public class DepartmentDAO implements IDeparmentDAO {

    private static final String SQL_INSERT = "INSERT INTO departamento (nombre, descripcion, idOrganizacion, estado) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE departamento SET nombre = ?, descripcion = ?, idOrganizacion = ? WHERE idDepartamento = ?";
    private static final String SQL_UPDATE_STATUS = "UPDATE departamento SET estado = ? WHERE idDepartamento = ?";
    private static final String SQL_DELETE = "DELETE FROM departamento WHERE idDepartamento = ?";
    private static final String SQL_SELECT = "SELECT * FROM departamento WHERE idDepartamento = ?";
    private static final String SQL_SELECT_ORGANIZATION_ID_BY_DEPARTMENT_ID = "SELECT idOrganizacion FROM departamento WHERE idDepartamento = ?";
    private static final String SQL_SELECT_BY_ORGANIZATION_ID = "SELECT * FROM departamento WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM departamento";

    public boolean insertDepartment(DepartmentDTO department) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, department.getName());
            statement.setString(2, department.getDescription());
            statement.setInt(3, department.getOrganizationId());
            statement.setInt(4, department.getStatus());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateDepartment(DepartmentDTO department) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {
            statement.setString(1, department.getName());
            statement.setString(2, department.getDescription());
            statement.setInt(3, department.getOrganizationId());
            statement.setInt(4, department.getDepartmentId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateDepartmentStatus(int departmentId, int status) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_STATUS)) {
            statement.setInt(1, status);
            statement.setInt(2, departmentId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteDepartment(int departmentId) throws SQLException, IOException {
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, departmentId);
            return statement.executeUpdate() > 0;
        }
    }

    public DepartmentDTO searchDepartmentById(int departmentId) throws SQLException, IOException {
        DepartmentDTO department = new DepartmentDTO(-1, "N/A", "N/A", -1, 0);
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT)) {
            statement.setInt(1, departmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    department = new DepartmentDTO(
                            resultSet.getInt("idDepartamento"),
                            resultSet.getString("nombre"),
                            resultSet.getString("descripcion"),
                            resultSet.getInt("idOrganizacion"),
                            resultSet.getInt("estado")
                    );
                }
            }
        }
        return department;
    }

    public int getOrganizationIdByDepartmentId(int departmentId) throws SQLException, IOException {
        int organizationId = -1;
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ORGANIZATION_ID_BY_DEPARTMENT_ID)) {
            statement.setInt(1, departmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    organizationId = resultSet.getInt("idOrganizacion");
                }
            }
        }
        return organizationId;
    }

    public List<DepartmentDTO> getAllDepartmentsByOrganizationId(int organizationId) throws SQLException, IOException {
        List<DepartmentDTO> departments = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ORGANIZATION_ID)) {
            statement.setInt(1, organizationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    departments.add(new DepartmentDTO(
                            resultSet.getInt("idDepartamento"),
                            resultSet.getString("nombre"),
                            resultSet.getString("descripcion"),
                            resultSet.getInt("idOrganizacion"),
                            resultSet.getInt("estado")
                    ));
                }
            }
        }
        return departments;
    }

    public List<DepartmentDTO> getAllDepartments() throws SQLException, IOException {
        List<DepartmentDTO> departments = new ArrayList<>();
        try (ConnectionDataBase connectionDataBase = new ConnectionDataBase();
             Connection connection = connectionDataBase.connectDataBase();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                departments.add(new DepartmentDTO(
                        resultSet.getInt("idDepartamento"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getInt("idOrganizacion"),
                        resultSet.getInt("estado")
                ));
            }
        }
        return departments;
    }
}