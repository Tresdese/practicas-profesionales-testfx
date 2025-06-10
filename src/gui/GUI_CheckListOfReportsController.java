package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.DAO.EvidenceDAO;
import logic.DAO.ReportDAO;
import logic.DTO.ReportDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GUI_CheckListOfReportsController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(GUI_CheckListOfReportsController.class);

    @FXML
    private TableView<ReportDTO> tableReports;
    @FXML
    private TableColumn<ReportDTO, String> columnNumber;
    @FXML
    private TableColumn<ReportDTO, java.util.Date> columnDate;
    @FXML
    private TableColumn<ReportDTO, Integer> columnHours;
    @FXML
    private TableColumn<ReportDTO, String> columnObjective;
    @FXML
    private TableColumn<ReportDTO, String> columnResult;
    @FXML
    private TableColumn<ReportDTO, Void> columnEvidence;

    private String studentTuition;

    public void setStudentTuition(String tuition) {
        this.studentTuition = (tuition != null) ? tuition.trim() : null;
        if (tableReports != null) {
            javafx.application.Platform.runLater(this::loadReports);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnNumber.setCellValueFactory(new PropertyValueFactory<>("numberReport"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        columnHours.setCellValueFactory(new PropertyValueFactory<>("totalHours"));
        columnObjective.setCellValueFactory(new PropertyValueFactory<>("generalObjective"));
        columnResult.setCellValueFactory(new PropertyValueFactory<>("obtainedResult"));

        columnEvidence.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Ver Evidencia");
            {
                btn.setOnAction(event -> {
                    ReportDTO report = getTableView().getItems().get(getIndex());
                    String evidenceUrl = getEvidenceUrlById(report.getIdEvidence());
                    if (evidenceUrl != null && !evidenceUrl.isEmpty()) {
                        openEvidenceUrl(evidenceUrl);
                    } else {
                        LOGGER.warn("No se encontró URL de evidencia para el id: {}", report.getIdEvidence());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        if (studentTuition != null) {
            javafx.application.Platform.runLater(this::loadReports);
        }
    }

    private void loadReports() {
        if (studentTuition == null) {
            LOGGER.warn("No se ha asignado matrícula para filtrar reportes.");
            tableReports.getItems().clear();
            return;
        }
        try {
            ReportDAO reportDAO = new ReportDAO();
            List<ReportDTO> allReports = reportDAO.getAllReports();
            List<ReportDTO> studentReports = allReports.stream()
                    .filter(r -> {
                        String reportTuition = (r.getTuition() != null) ? r.getTuition().trim() : "";
                        return reportTuition.equalsIgnoreCase(studentTuition);
                    })
                    .collect(Collectors.toList());
            tableReports.getItems().setAll(studentReports);
        } catch (SQLException e) {
            LOGGER.error("Error de base de datos al cargar reportes: {}", e.getMessage(), e);
        } catch (NullPointerException e) {
            LOGGER.error("Se encontró un valor nulo inesperado al cargar reportes: {}", e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar reportes: {}", e.getMessage(), e);
        }
    }

    private String getEvidenceUrlById(String idEvidence) {
        try {
            EvidenceDAO evidenceDAO = new EvidenceDAO();
            int id = Integer.parseInt(idEvidence);
            return evidenceDAO.searchEvidenceById(id).getRoute();
        } catch (NumberFormatException e) {
            LOGGER.error("El id de evidencia no es un número válido: {} - {}", idEvidence, e.getMessage(), e);
        } catch (NullPointerException e) {
            LOGGER.error("No se encontró evidencia para el id proporcionado: {} - {}", idEvidence, e.getMessage(), e);
        } catch (SQLException e) {
            LOGGER.error("Error de base de datos al buscar evidencia: {}", e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("No se pudo obtener la URL de la evidencia para id {}: {}", idEvidence, e.getMessage(), e);
        }
        return null;
    }

    private void openEvidenceUrl(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                LOGGER.error("Desktop no soportado para abrir URLs.");
            }
        } catch (IOException e) {
            LOGGER.error("Error de IO al abrir la URL de la evidencia: {}", e.getMessage(), e);
        } catch (URISyntaxException e) {
            LOGGER.error("La URL de la evidencia tiene un formato inválido: {}", e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("No se pudo abrir la URL de la evidencia: {}", e.getMessage(), e);
        }
    }
}