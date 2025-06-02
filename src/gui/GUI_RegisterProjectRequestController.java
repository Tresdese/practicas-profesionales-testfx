package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.ProjectRequestDAO;
import logic.DAO.RepresentativeDAO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.ProjectStatus;
import logic.DTO.RepresentativeDTO;
import logic.DTO.StudentDTO;

import java.sql.SQLException;
import java.util.List;

public class GUI_RegisterProjectRequestController {
    private StudentDTO student;

    @FXML private ComboBox<LinkedOrganizationDTO> comboOrganization;
    @FXML private ComboBox<RepresentativeDTO> comboRepresentative;
    @FXML private ComboBox<ProjectDTO> comboProject;

    @FXML private TextArea fieldDescription;
    @FXML private TextArea fieldGeneralObjective;
    @FXML private TextArea fieldImmediateObjectives;
    @FXML private TextArea fieldMediateObjectives;
    @FXML private TextArea fieldMethodology;
    @FXML private TextArea fieldResources;
    @FXML private TextArea fieldActivities;
    @FXML private TextArea fieldResponsibilities;
    @FXML private TextField fieldDuration;
    @FXML private CheckBox mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck, sundayCheck;
    @FXML private TextField fieldScheduleTime;
    @FXML private TextField fieldDirectUsers;
    @FXML private TextField fieldIndirectUsers;
    @FXML private Button btnRegister;
    @FXML private Label statusLabel;

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    @FXML
    private void initialize() {
        loadOrganizations();
        comboOrganization.setOnAction(e -> {
            loadRepresentatives();
            loadProjects();
        });
        fieldDuration.setText("420");
        fieldDuration.setDisable(true);
    }

    private void loadOrganizations() {
        try {
            LinkedOrganizationDAO orgDao = new LinkedOrganizationDAO();
            List<LinkedOrganizationDTO> orgs = orgDao.getAllLinkedOrganizations();
            comboOrganization.setItems(FXCollections.observableArrayList(orgs));
        } catch (SQLException e) {
            setStatus("Error cargando organizaciones.", true);
        }
    }

    private void loadRepresentatives() {
        comboRepresentative.getItems().clear();
        LinkedOrganizationDTO org = comboOrganization.getValue();
        if (org != null) {
            try {
                RepresentativeDAO repDao = new RepresentativeDAO();
                List<RepresentativeDTO> reps = repDao.getRepresentativesByOrganization(org.getIddOrganization());
                comboRepresentative.setItems(FXCollections.observableArrayList(reps));
            } catch (SQLException e) {
                setStatus("Error cargando representantes.", true);
            }
        }
    }

    private void loadProjects() {
        comboProject.getItems().clear();
        LinkedOrganizationDTO org = comboOrganization.getValue();
        if (org != null) {
            try {
                ProjectDAO projectDao = new ProjectDAO();
                List<ProjectDTO> projects = projectDao.getAllProjects();
                int orgId = Integer.parseInt(org.getIddOrganization());
                projects.removeIf(p -> p.getIdOrganization() != orgId);
                comboProject.setItems(FXCollections.observableArrayList(projects));

                comboProject.setConverter(new StringConverter<ProjectDTO>() {
                    @Override
                    public String toString(ProjectDTO project) {
                        return project == null ? "" : project.getName();
                    }

                    @Override
                    public ProjectDTO fromString(String string) {
                        return null; // No es necesario para este caso
                    }
                });
            } catch (Exception e) {
                setStatus("Error cargando proyectos.", true);
            }
        }
    }

    @FXML
    private void handleRegisterProjectRequest() {
        if (!validateFields()) return;

        try {
            LinkedOrganizationDTO org = comboOrganization.getValue();
            RepresentativeDTO rep = comboRepresentative.getValue();
            ProjectDTO project = comboProject.getValue();

            ProjectRequestDTO request = new ProjectRequestDTO(
                    0,
                    student.getTuiton(),
                    org.getIddOrganization(),
                    rep.getIdRepresentative(),
                    String.valueOf(project.getIdProject()), // Usar el ID del proyecto en lugar del nombre
                    fieldDescription.getText(),
                    fieldGeneralObjective.getText(),
                    fieldImmediateObjectives.getText(),
                    fieldMediateObjectives.getText(),
                    fieldMethodology.getText(),
                    fieldResources.getText(),
                    fieldActivities.getText(),
                    fieldResponsibilities.getText(),
                    420,
                    getScheduleDays(),
                    Integer.parseInt(fieldDirectUsers.getText()),
                    Integer.parseInt(fieldIndirectUsers.getText()),
                    ProjectStatus.pendiente.name(),
                    ""
            );

            ProjectRequestDAO dao = new ProjectRequestDAO();
            boolean success = dao.insertProjectRequest(request);

            if (success) {
                setStatus("Solicitud registrada correctamente.", false);
                clearFields();
            } else {
                setStatus("Error al registrar la solicitud.", true);
            }
        } catch (NumberFormatException e) {
            setStatus("Verifica los campos numéricos.", true);
        } catch (Exception e) {
            setStatus("Error: " + e.getMessage(), true);
        }
    }

    private String getScheduleDays() {
        StringBuilder days = new StringBuilder();
        if (mondayCheck.isSelected()) days.append("Lun ");
        if (tuesdayCheck.isSelected()) days.append("Mar ");
        if (wednesdayCheck.isSelected()) days.append("Mié ");
        if (thursdayCheck.isSelected()) days.append("Jue ");
        if (fridayCheck.isSelected()) days.append("Vie ");
        if (saturdayCheck.isSelected()) days.append("Sáb ");
        if (sundayCheck.isSelected()) days.append("Dom ");
        String horario = fieldScheduleTime.getText().trim();
        return days.toString().trim() + (horario.isEmpty() ? "" : " " + horario);
    }

    private boolean validateFields() {
        if (comboOrganization.getValue() == null ||
                comboRepresentative.getValue() == null ||
                comboProject.getValue() == null ||
                fieldGeneralObjective.getText().isEmpty() ||
                fieldImmediateObjectives.getText().isEmpty() ||
                fieldMediateObjectives.getText().isEmpty() ||
                fieldMethodology.getText().isEmpty() ||
                fieldResources.getText().isEmpty() ||
                fieldActivities.getText().isEmpty() ||
                fieldResponsibilities.getText().isEmpty() ||
                // Validar al menos un día y horario
                (!mondayCheck.isSelected() && !tuesdayCheck.isSelected() && !wednesdayCheck.isSelected() &&
                        !thursdayCheck.isSelected() && !fridayCheck.isSelected() && !saturdayCheck.isSelected() && !sundayCheck.isSelected()) ||
                fieldScheduleTime.getText().isEmpty() ||
                fieldDirectUsers.getText().isEmpty() ||
                fieldIndirectUsers.getText().isEmpty()) {
            setStatus("Completa todos los campos obligatorios.", true);
            return false;
        }
        return true;
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    private void clearFields() {
        fieldDescription.clear();
        fieldGeneralObjective.clear();
        fieldImmediateObjectives.clear();
        fieldMediateObjectives.clear();
        fieldMethodology.clear();
        fieldResources.clear();
        fieldActivities.clear();
        fieldResponsibilities.clear();
        // fieldDuration no se limpia porque es fijo
        mondayCheck.setSelected(false);
        tuesdayCheck.setSelected(false);
        wednesdayCheck.setSelected(false);
        thursdayCheck.setSelected(false);
        fridayCheck.setSelected(false);
        saturdayCheck.setSelected(false);
        sundayCheck.setSelected(false);
        fieldScheduleTime.clear();
        fieldDirectUsers.clear();
        fieldIndirectUsers.clear();
        comboOrganization.getSelectionModel().clearSelection();
        comboRepresentative.getItems().clear();
        comboProject.getItems().clear();
    }
}