package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logic.DAO.LinkedOrganizationDAO;
import logic.DAO.ProjectDAO;
import logic.DAO.ProjectRequestDAO;
import logic.DAO.RepresentativeDAO;
import logic.DTO.LinkedOrganizationDTO;
import logic.DTO.ProjectDTO;
import logic.DTO.ProjectRequestDTO;
import logic.DTO.RepresentativeDTO;
import logic.DTO.StudentDTO;
import logic.DTO.ProjectStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GUI_RegisterProjectRequestController {
    private static final Logger logger = LogManager.getLogger(GUI_RegisterProjectRequestController.class);

    @FXML
    private ComboBox<LinkedOrganizationDTO> organizationComboBox;
    @FXML
    private ComboBox<RepresentativeDTO> representativeComboBox;
    @FXML
    private ComboBox<ProjectDTO> projectComboBox;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextArea generalObjectiveArea;
    @FXML
    private TextArea immediateObjectivesArea;
    @FXML
    private TextArea mediateObjectivesArea;
    @FXML
    private TextArea methodologyArea;
    @FXML
    private TextArea resourcesArea;
    @FXML
    private TextArea activitiesArea;
    @FXML
    private TextArea responsibilitiesArea;
    @FXML
    private TextField scheduleTimeField;
    @FXML
    private TextField directUsersField;
    @FXML
    private TextField indirectUsersField;
    @FXML
    private CheckBox mondayCheck;
    @FXML
    private CheckBox tuesdayCheck;
    @FXML
    private CheckBox wednesdayCheck;
    @FXML
    private CheckBox thursdayCheck;
    @FXML
    private CheckBox fridayCheck;
    @FXML
    private CheckBox saturdayCheck;
    @FXML
    private CheckBox sundayCheck;
    @FXML
    private Label statusLabel;
    @FXML
    private Label descriptionCharCountLabel;
    @FXML
    private Label generalObjectiveCharCountLabel;
    @FXML
    private Label immediateObjectivesCharCountLabel;
    @FXML
    private Label mediateObjectivesCharCountLabel;
    @FXML
    private Label methodologyCharCountLabel;
    @FXML
    private Label resourcesAreaCharCountLabel;
    @FXML
    private Label activitiesCharCountLabel;
    @FXML
    private Label responsibilitiesCharCountLabel;
    @FXML
    private TextField durationField;

    private static final int MAX_CARACTER_LIMIT = 300;
    private static final String REGEX_SCHEDULE_TIME = "^([01]?\\d|2[0-3])?(:[0-5]?\\d)?(-([01]?\\d|2[0-3])?(:[0-5]?\\d)?)?$";
    private StudentDTO student;

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    @FXML
    public void initialize() {
        try {
            configureTextFormatters();
            configureCharCountLabels();
            configureScheduleTimeField();
            configureNumericFields();
            loadOrganizations();

            organizationComboBox.setOnAction(event -> {
                loadRepresentatives();
                loadProjects();
            });
        } catch (NullPointerException e) {
            logger.error("Error al inicializar el controlador: {}", e.getMessage(), e);
            setStatus("Error al inicializar la interfaz.", true);
        }
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
            organizationComboBox.setItems(FXCollections.observableArrayList(orgs));
            logger.info("Organizaciones cargadas correctamente.");
        } catch (SQLException e) {
            logger.error("Error cargando organizaciones: {}", e.getMessage(), e);
            setStatus("Error cargando organizaciones.", true);
        }
    }

    private void loadRepresentatives() {
        representativeComboBox.getItems().clear();
        LinkedOrganizationDTO org = organizationComboBox.getValue();
        if (org != null) {
            try {
                logic.DAO.DepartmentDAO deptDao = new logic.DAO.DepartmentDAO();
                List<logic.DTO.DepartmentDTO> departments = deptDao.getAllDepartmentsByOrganizationId(Integer.parseInt(org.getIdOrganization()));
                RepresentativeDAO repDao = new RepresentativeDAO();
                List<RepresentativeDTO> allReps = new java.util.ArrayList<>();
                for (logic.DTO.DepartmentDTO dept : departments) {
                    List<RepresentativeDTO> reps = repDao.getRepresentativesByDepartment(String.valueOf(dept.getDepartmentId()));
                    allReps.addAll(reps);
                }
                representativeComboBox.setItems(FXCollections.observableArrayList(allReps));
                logger.info("Representantes cargados para la organización {}", org.getName());
            } catch (SQLException e) {
                logger.error("Error cargando representantes: {}", e.getMessage(), e);
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
        projectComboBox.getItems().clear();
        LinkedOrganizationDTO org = organizationComboBox.getValue();
        if (org != null) {
            try {
                ProjectDAO projectDao = new ProjectDAO();
                List<ProjectDTO> projects = projectDao.getAllProjects();
                int orgId = Integer.parseInt(org.getIdOrganization());
                projects.removeIf(p -> p.getIdOrganization() != orgId);
                projectComboBox.setItems(FXCollections.observableArrayList(projects));

                projectComboBox.setConverter(new javafx.util.StringConverter<ProjectDTO>() {
                    @Override
                    public String toString(ProjectDTO project) {
                        return project == null ? "" : project.getName();
                    }

                    @Override
                    public ProjectDTO fromString(String string) {
                        return null;
                    }
                });
                logger.info("Proyectos cargados para la organización {}", org.getName());
            } catch (Exception e) {
                logger.error("Error cargando proyectos: {}", e.getMessage(), e);
                setStatus("Error cargando proyectos.", true);
            }
        }
    }

    @FXML
    private void handleRegisterProjectRequest() {
        if (!validateFields()) return;

        try {
            LinkedOrganizationDTO org = organizationComboBox.getValue();
            RepresentativeDTO rep = representativeComboBox.getValue();
            ProjectDTO project = projectComboBox.getValue();

            ProjectRequestDTO request = new ProjectRequestDTO(
                    0,
                    student.getTuition(),
                    org.getIdOrganization(),
                    rep.getIdRepresentative(),
                    project.getName(),
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
                logger.info("Solicitud de proyecto registrada correctamente para el estudiante {}", student.getTuition());
                statusLabel.setText("Solicitud registrada correctamente.");
                clearFields();
            } else {
                logger.warn("Error al registrar la solicitud de proyecto para el estudiante {}", student.getTuition());
                statusLabel.setText("Error al registrar la solicitud.");
            }
        } catch (Exception e) {
            logger.error("Error al registrar la solicitud de proyecto: {}", e.getMessage(), e);
            setStatus("Error al registrar la solicitud.", true);
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
        String schedule = scheduleTimeField.getText().trim();
        return days.toString().trim() + (schedule.isEmpty() ? "" : " " + schedule);
    }

    private boolean validateFields() {
        if (organizationComboBox.getValue() == null ||
                representativeComboBox.getValue() == null ||
                projectComboBox.getValue() == null ||
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
            logger.warn("Validación fallida: campos obligatorios incompletos.");
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
        organizationComboBox.getSelectionModel().clearSelection();
        representativeComboBox.getItems().clear();
        projectComboBox.getItems().clear();
        durationField.setText("420");
        logger.info("Campos del formulario limpiados.");
    }
}