package logic.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import logic.DTO.LinkedOrganizationDTO;

public class LinkedOrganizationDAO {
    private final static String SQL_INSERT = "INSERT INTO organizacion_vinculada (idOrganizacion, nombre, direccion) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE = "UPDATE organizacion_vinculada SET nombre = ?, direccion = ? WHERE idOrganizacion = ?";
    private final static String SQL_DELETE = "DELETE FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private final static String SQL_SELECT = "SELECT * FROM organizacion_vinculada WHERE idOrganizacion = ?";
    private final static String SQL_SELECT_ALL = "SELECT * FROM organizacion_vinculada";

    public boolean insertLinkedOrganization(LinkedOrganizationDTO organization, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_INSERT)) {
            ps.setString(1, organization.getIddOrganization());
            ps.setString(2, organization.getName());
            ps.setString(3, organization.getAdddress());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateLinkedOrganization(LinkedOrganizationDTO organization, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, organization.getName());
            ps.setString(2, organization.getAdddress());
            ps.setString(3, organization.getIddOrganization());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteLinkedOrganization(String iddOrganization, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setString(1, iddOrganization);
            return ps.executeUpdate() > 0;
        }
    }

    public LinkedOrganizationDTO getLinkedOrganization(String iddOrganization, Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT)) {
            ps.setString(1, iddOrganization);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LinkedOrganizationDTO(rs.getString("idOrganizacion"), rs.getString("nombre"), rs.getString("direccion"));
                }
            }
        }
        return null;
    }

    public List<LinkedOrganizationDTO> getAllLinkedOrganizations(Connection connection) throws SQLException {
        List<LinkedOrganizationDTO> organizations = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                organizations.add(new LinkedOrganizationDTO(rs.getString("idOrganizacion"), rs.getString("nombre"), rs.getString("direccion")));
            }
        }
        return organizations;
    }
}
