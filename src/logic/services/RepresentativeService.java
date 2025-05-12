package logic.services;

import logic.DAO.RepresentativeDAO;
import logic.DTO.RepresentativeDTO;
import logic.exceptions.RepeatedEmail;
import logic.exceptions.RepeatedId;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RepresentativeService {
    private final RepresentativeDAO representativeDAO;

    public RepresentativeService(Connection connection) {
        this.representativeDAO = new RepresentativeDAO(connection);
    }

    public boolean registerRepresentative(RepresentativeDTO representative) throws SQLException, RepeatedEmail {

        if (representativeDAO.isRepresentativeRegistered(representative.getIdRepresentative())) {
            throw new RepeatedId("La ID ya está registrada.");
        }

        if (representativeDAO.isRepresentativeEmailRegistered(representative.getEmail())) {
            throw new RepeatedEmail("El correo electrónico ya está registrado.");
        }

        boolean success = representativeDAO.insertRepresentative(representative);
        if (!success) {
            throw new SQLException("No se pudo registrar al representante.");
        }

        return success;
    }

    public boolean updateRepresentative(RepresentativeDTO representative) throws SQLException {
        boolean success = representativeDAO.updateRepresentative(representative);
        if (!success) {
            throw new SQLException("No se pudo actualizar al representante.");
        }

        return success;
    }

    public List<RepresentativeDTO> getAllRepresentatives() throws SQLException {
        return representativeDAO.getAllRepresentatives();
    }

    public RepresentativeDTO searchRepresentativeById(String id) throws SQLException {
        return representativeDAO.searchRepresentativeById(id);
    }
}
