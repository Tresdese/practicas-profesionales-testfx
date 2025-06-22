package logic.interfaces;

import logic.DTO.EvidenceDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IEvidenceDAO {
    boolean insertEvidence(EvidenceDTO evidence) throws SQLException, IOException;

    boolean updateEvidence(EvidenceDTO evidence) throws SQLException, IOException;

    boolean deleteEvidence(int idEvidence) throws SQLException, IOException;

    EvidenceDTO searchEvidenceById(int idEvidence) throws SQLException, IOException;

    List<EvidenceDTO> getAllEvidences() throws SQLException, IOException;

    int getNextEvidenceId() throws SQLException, IOException;
}
