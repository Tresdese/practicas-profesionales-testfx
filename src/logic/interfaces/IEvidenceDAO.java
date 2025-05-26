package logic.interfaces;

import logic.DTO.EvidenceDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IEvidenceDAO {
    boolean insertEvidence(EvidenceDTO evidence) throws SQLException;

    boolean updateEvidence(EvidenceDTO evidence) throws SQLException;

    boolean deleteEvidence(int idEvidence) throws SQLException;

    EvidenceDTO searchEvidenceById(int idEvidence) throws SQLException;

    List<EvidenceDTO> getAllEvidences() throws SQLException;
}
