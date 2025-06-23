package logic.services;

import logic.DAO.RepresentativeDAO;
import logic.DTO.RepresentativeDTO;
import logic.exceptions.RepeatedEmail;
import logic.exceptions.RepeatedId;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RepresentativeService {
    private final RepresentativeDAO representativeDAO;

    public RepresentativeService(Connection connection) {
        this.representativeDAO = new RepresentativeDAO();
    }

    public boolean registerRepresentative(RepresentativeDTO representative) throws SQLException, IOException, RepeatedEmail {

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

    public boolean updateRepresentative(RepresentativeDTO representative) throws SQLException, IOException, RepeatedEmail {
        boolean success = representativeDAO.updateRepresentative(representative);
        if (!success) {
            throw new SQLException("No se pudo actualizar al representante.");
        }

        return success;
    }

    public boolean updateRepresentativeStatus(String idRepresentative, int status) throws SQLException, IOException {
        boolean success = representativeDAO.updateRepresentativeStatus(idRepresentative, status);
        if (!success) {
            throw new SQLException("No se pudo actualizar el estado del representante.");
        }

        return success;
    }

    public List<RepresentativeDTO> getAllRepresentatives() throws SQLException, IOException {
        return representativeDAO.getAllRepresentatives();
    }

    public RepresentativeDTO searchRepresentativeById(String id) throws SQLException, IOException {
        return representativeDAO.searchRepresentativeById(id);
    }

    public RepresentativeDTO searchRepresentativeByFullname(String names, String surnames) throws SQLException, IOException {
        return representativeDAO.searchRepresentativeByFullname(names, surnames);
    }
}
