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

    @FXML private ComboBox<LinkedOrganizationDTO> organizationCombo;
    @FXML private ComboBox<RepresentativeDTO> representativeCombo;
    @FXML private ComboBox<ProjectDTO> projectCombo;

    @FXML private TextArea descriptionArea;
    @FXML private TextArea generalObjectiveArea;
    @FXML private TextArea immediateObjectivesArea;
    @FXML private TextArea mediateObjectivesArea;
    @FXML private TextArea methodologyArea;
    @FXML private TextArea resourcesArea;
    @FXML private TextArea activitiesArea;
    @FXML private TextArea responsibilitiesArea;
    @FXML private TextField durationField;
    @FXML private CheckBox mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck, sundayCheck;
    @FXML private TextField scheduleTimeField;
    @FXML private TextField directUsersField;
    @FXML private TextField indirectUsersField;
    @FXML private Button registerButton;
    @FXML private Label statusLabel,
            descriptionCharCountLabel, generalObjectiveCharCountLabel, immediateObjectivesCharCountLabel,
            mediateObjectivesCharCountLabel, methodologyCharCountLabel, resourcesAreaCharCountLabel,
            activitiesCharCountLabel, responsibilitiesCharCountLabel;

    private static final int MAX_CARACTER_LIMIT = 300;

    private static final String REGEX_SCHEDULE_TIME = "^([01]?\\d|2[0-3])?(:[0-5]?\\d)?(-([01]?\\d|2[0-3])?(:[0-5]?\\d)?)?$";

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    @FXML
    private void initialize() {
        loadOrganizations();
        organizationCombo.setOnAction(e -> {
            loadRepresentatives();
            loadProjects();
        });
        configureTextFormatters();
        configureCharCountLabels();
        configureScheduleTimeField();
        configureNumericFields();
        durationField.setText("420");
        durationField.setDisable(true);
    }

    private void configureTextFormatters() {
        configureTextFormatter(descriptionArea, MAX_CARACTER_LIMIT);
        configureTextFormatter(generalObjectiveArea, MAX_CARACTER_LIMIT);
        configureTextFormatter(immediateObjectivesArea, MAX_CARACTER_LIMIT);
        configureTextFormatter(mediateObjectivesArea, MAX_CARACTER_LIMIT);
        configureTextFormatter(methodologyArea, MAX_CARACTER_LIMIT);
        configureTextFormatter(resourcesArea, MAX_CARACTER_LIMIT);
        configureTextFormatter(activitiesArea, MAX_CARACTER_LIMIT);
        configureTextFormatter(responsibilitiesArea, MAX_CARACTER_LIMIT);
    }

    private void configureTextFormatter(TextArea textArea, int maxLength) {
        textArea.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= maxLength ? change : null
        ));
    }

    private void configureCharCountLabels() {
        configureCharCount(descriptionArea, descriptionCharCountLabel, MAX_CARACTER_LIMIT);
        configureCharCount(generalObjectiveArea, generalObjectiveCharCountLabel, MAX_CARACTER_LIMIT);
        configureCharCount(immediateObjectivesArea, immediateObjectivesCharCountLabel, MAX_CARACTER_LIMIT);
        configureCharCount(mediateObjectivesArea, mediateObjectivesCharCountLabel, MAX_CARACTER_LIMIT);
        configureCharCount(methodologyArea, methodologyCharCountLabel, MAX_CARACTER_LIMIT);
        configureCharCount(resourcesArea, resourcesAreaCharCountLabel, MAX_CARACTER_LIMIT);
        configureCharCount(activitiesArea, activitiesCharCountLabel, MAX_CARACTER_LIMIT);
        configureCharCount(responsibilitiesArea, responsibilitiesCharCountLabel, MAX_CARACTER_LIMIT);
    }

    private void configureCharCount(TextArea textArea, Label charCountLabel, int maxLength) {
        charCountLabel.setText("0/" + maxLength);
        textArea.textProperty().addListener((observable, oldText, newText) ->
                charCountLabel.setText(newText.length() + "/" + maxLength)
        );
    }

    private void configureScheduleTimeField() {
        scheduleTimeField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches(REGEX_SCHEDULE_TIME) || newText.endsWith(":")) {
                return change;
            }
            change.setText("");
            return change;
        }));
    }

    private void loadOrganizations() {
        try {
            LinkedOrganizationDAO orgDao = new LinkedOrganizationDAO();
            List<LinkedOrganizationDTO> orgs = orgDao.getAllLinkedOrganizations();
            organizationCombo.setItems(FXCollections.observableArrayList(orgs));
        } catch (SQLException e) {
            setStatus("Error cargando organizaciones.", true);
        }
    }

    private void loadRepresentatives() {
        representativeCombo.getItems().clear();
        LinkedOrganizationDTO org = organizationCombo.getValue();
        if (org != null) {
            try {
                RepresentativeDAO repDao = new RepresentativeDAO();
                List<RepresentativeDTO> reps = repDao.getRepresentativesByOrganization(org.getIdOrganization());
                representativeCombo.setItems(FXCollections.observableArrayList(reps));
            } catch (SQLException e) {
                setStatus("Error cargando representantes.", true);
            }
        }
    }

    private void configureNumericFields() {
        directUsersField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            change.setText("");
            return change;
        }));

        indirectUsersField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            change.setText("");
            return change;
        }));
    }

    private void loadProjects() {
        projectCombo.getItems().clear();
        LinkedOrganizationDTO org = organizationCombo.getValue();
        if (org != null) {
            try {
                ProjectDAO projectDao = new ProjectDAO();
                List<ProjectDTO> projects = projectDao.getAllProjects();
                int orgId = Integer.parseInt(org.getIdOrganization());
                projects.removeIf(p -> p.getIdOrganization() != orgId);
                projectCombo.setItems(FXCollections.observableArrayList(projects));

                projectCombo.setConverter(new StringConverter<ProjectDTO>() {
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
            LinkedOrganizationDTO org = organizationCombo.getValue();
            RepresentativeDTO rep = representativeCombo.getValue();
            ProjectDTO project = projectCombo.getValue();

            ProjectRequestDTO request = new ProjectRequestDTO(
                    0,
                    student.getTuition(),
                    org.getIdOrganization(),
                    rep.getIdRepresentative(),
                    project.getName(), // Corregido: se usa el nombre del proyecto
                    descriptionArea.getText(),
                    generalObjectiveArea.getText(),
                    immediateObjectivesArea.getText(),
                    mediateObjectivesArea.getText(),
                    methodologyArea.getText(),
                    resourcesArea.getText(),
                    activitiesArea.getText(),
                    responsibilitiesArea.getText(),
                    420,
                    getScheduleDays(),
                    Integer.parseInt(directUsersField.getText()),
                    Integer.parseInt(indirectUsersField.getText()),
                    ProjectStatus.pendiente.name(),
                    ""
            );

            ProjectRequestDAO dao = new ProjectRequestDAO();
            boolean success = dao.insertProjectRequest(request);

            if (success) {
                statusLabel.setText("Solicitud registrada correctamente.");
                clearFields();
            } else {
                statusLabel.setText("Error al registrar la solicitud.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
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
        String horario = scheduleTimeField.getText().trim();
        return days.toString().trim() + (horario.isEmpty() ? "" : " " + horario);
    }

    private boolean validateFields() {
        if (organizationCombo.getValue() == null ||
                representativeCombo.getValue() == null ||
                projectCombo.getValue() == null ||
                generalObjectiveArea.getText().isEmpty() ||
                immediateObjectivesArea.getText().isEmpty() ||
                mediateObjectivesArea.getText().isEmpty() ||
                methodologyArea.getText().isEmpty() ||
                resourcesArea.getText().isEmpty() ||
                activitiesArea.getText().isEmpty() ||
                responsibilitiesArea.getText().isEmpty() ||
                (!mondayCheck.isSelected() && !tuesdayCheck.isSelected() && !wednesdayCheck.isSelected() &&
                        !thursdayCheck.isSelected() && !fridayCheck.isSelected() && !saturdayCheck.isSelected() && !sundayCheck.isSelected()) ||
                scheduleTimeField.getText().isEmpty() ||
                !scheduleTimeField.getText().matches(REGEX_SCHEDULE_TIME) ||
                directUsersField.getText().isEmpty() ||
                indirectUsersField.getText().isEmpty()) {
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
        descriptionArea.clear();
        generalObjectiveArea.clear();
        immediateObjectivesArea.clear();
        mediateObjectivesArea.clear();
        methodologyArea.clear();
        resourcesArea.clear();
        activitiesArea.clear();
        responsibilitiesArea.clear();
        // fieldDuration no se limpia porque es fijo
        mondayCheck.setSelected(false);
        tuesdayCheck.setSelected(false);
        wednesdayCheck.setSelected(false);
        thursdayCheck.setSelected(false);
        fridayCheck.setSelected(false);
        saturdayCheck.setSelected(false);
        sundayCheck.setSelected(false);
        scheduleTimeField.clear();
        directUsersField.clear();
        indirectUsersField.clear();
        organizationCombo.getSelectionModel().clearSelection();
        representativeCombo.getItems().clear();
        projectCombo.getItems().clear();
    }
}