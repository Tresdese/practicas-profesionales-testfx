package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.DTO.Role;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUI_MenuUserController {

    private static final Logger logger = LogManager.getLogger(GUI_MenuUserController.class);

    private static boolean enabledEvaluation = false;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button viewStudentListButton;

    @FXML
    private Button checkPresentationGradeButton;

    @FXML
    private Button evaluatePresentationButton;

    @FXML
    private Button viewAcademicListButton;

    @FXML
    private Button viewOrganizationListButton;

    @FXML
    private Button viewRepresentativeListButton;

    @FXML
    private Button viewProjectListButton;

    @FXML
    private Button viewProjectRequestButton;

    @FXML
    private Button viewPeriodListButton;

    @FXML
    private Button viewGroupListButton;

    @FXML
    private Button manageAssessmentCriteriaButton;

    @FXML
    private Button manageSelfAssessmentCriteriaButton;

    @FXML
    private Button manageActivityButton;

    @FXML
    private Button ableRegisterEvaluationButton;

    @FXML
    private Button logoutButton;

    private int actualUserId;
    private String userRole;

    public void setUserName(String userName) {
        welcomeLabel.setText("Hola, " + userName);
    }

    public void setActualUserId(int id) {
        this.actualUserId = id;
    }

    void setButtonVisibility(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    public void setUserRole(Role role) {
        this.userRole = role.name();

        if (role == Role.ACADEMICO_EVALUADOR) {
            setButtonVisibility(viewStudentListButton, false);
            setButtonVisibility(viewAcademicListButton, false);
            setButtonVisibility(viewOrganizationListButton, false);
            setButtonVisibility(viewRepresentativeListButton, false);
            setButtonVisibility(viewProjectListButton, false);
            setButtonVisibility(viewProjectRequestButton, false);
            setButtonVisibility(checkPresentationGradeButton, enabledEvaluation);
            setButtonVisibility(evaluatePresentationButton, enabledEvaluation);
            setButtonVisibility(viewPeriodListButton, false);
            setButtonVisibility(viewGroupListButton, false);
            setButtonVisibility(manageAssessmentCriteriaButton, true);
            setButtonVisibility(manageSelfAssessmentCriteriaButton, true);
            setButtonVisibility(manageActivityButton, true);
            setButtonVisibility(ableRegisterEvaluationButton, false);

        } else if (role == Role.ACADEMICO) {
            setButtonVisibility(viewStudentListButton, true);
            setButtonVisibility(checkPresentationGradeButton, true);
            setButtonVisibility(evaluatePresentationButton, false);
            setButtonVisibility(viewAcademicListButton, false);
            setButtonVisibility(viewOrganizationListButton, false);
            setButtonVisibility(viewRepresentativeListButton, false);
            setButtonVisibility(viewProjectListButton, false);
            setButtonVisibility(viewProjectRequestButton, false);
            setButtonVisibility(viewPeriodListButton, true);
            setButtonVisibility(viewGroupListButton, true);
            setButtonVisibility(manageAssessmentCriteriaButton, false);
            setButtonVisibility(manageSelfAssessmentCriteriaButton, false);
            setButtonVisibility(manageActivityButton, false);
            setButtonVisibility(ableRegisterEvaluationButton, false);

        } else if (role == Role.COORDINADOR) {
            setButtonVisibility(viewStudentListButton, true);
            setButtonVisibility(checkPresentationGradeButton, true);
            setButtonVisibility(evaluatePresentationButton, false);
            setButtonVisibility(viewAcademicListButton, true);
            setButtonVisibility(viewOrganizationListButton, true);
            setButtonVisibility(viewRepresentativeListButton, true);
            setButtonVisibility(viewProjectListButton, true);
            setButtonVisibility(viewProjectRequestButton, true);
            setButtonVisibility(viewPeriodListButton, true);
            setButtonVisibility(viewGroupListButton, true);
            setButtonVisibility(manageAssessmentCriteriaButton, true);
            setButtonVisibility(manageSelfAssessmentCriteriaButton, true);
            setButtonVisibility(manageActivityButton, true);
            setButtonVisibility(ableRegisterEvaluationButton, true);
        }
    }

    @FXML
    private void handleCheckPresentationGradeButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckPresentationGrade.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Consultar Calificaciones de Presentación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de calificaciones: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleEvaluatePresentationButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfPresentations.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Calificar Presentación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de evaluación: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleViewStudentListButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfStudents.fxml"));
            Parent root = loader.load();

            GUI_CheckListOfStudentsController controller = loader.getController();
            controller.setUserRole(Role.valueOf(userRole));
            controller.setIdUserAcademic(actualUserId);

            Stage stage = new Stage();
            stage.setTitle("Lista de Estudiantes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de estudiantes: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleViewAcademicListButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckAcademicList.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Lista de Académicos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de académicos: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleViewOrganizationListButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListLinkedOrganization.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Lista de Organizaciones");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de organizaciones: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleViewRepresentativeListButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckRepresentativeList.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Lista de Representantes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de representantes: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleViewProjectListButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckProjectList.fxml"));
            Parent root = loader.load();
            GUI_CheckProjectListController controller = loader.getController();
            controller.setRole(Role.valueOf(userRole));

            Stage stage = new Stage();
            stage.setTitle("Lista de Proyectos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de proyectos: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleViewProjectRequestButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckProjectRequestList.fxml"));
            Parent root = loader.load();
            GUI_CheckProjectRequestListController controller = loader.getController();
            controller.setUserRole(Role.valueOf(userRole));

            Stage stage = new Stage();
            stage.setTitle("Lista de Solicitudes de Prácticas");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de solicitudes de prácticas: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleViewPeriodListButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfPeriods.fxml"));
            Parent root = loader.load();

            GUI_CheckListOfPeriodsController controller = loader.getController();
            controller.setUserRole(Role.valueOf(userRole));

            Stage stage = new Stage();
            stage.setTitle("Lista de Períodos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de períodos: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleViewGroupListButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckListOfGroups.fxml"));
            Parent root = loader.load();

            GUI_CheckListOfGroupsController controller = loader.getController();
            controller.setUserRole(Role.valueOf(userRole));

            Stage stage = new Stage();
            stage.setTitle("Lista de Grupos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de lista de grupos: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleManageAssessmentCriteriaButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageAssessmentCriteria.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestión de Criterios de Evaluación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de criterios de evaluación: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleManageSelfAssessmentCriteriaButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageSelfAssessmentCriteria.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestión de Criterios de Autoevaluación");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de criterios de autoevaluación: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleManageActivityButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_ManageActivity.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestión de Actividades");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir la ventana de actividades: {}", e.getMessage(), e);
        }
    }

    @FXML
    private void handleAbleRegisterEvaluationButton(ActionEvent event) {
        enabledEvaluation = !enabledEvaluation;
    }

    @FXML
    private void handleLogoutButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_Login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Inicio de Sesión");
            stage.show();
        } catch (Exception e) {
            logger.error("Error al cerrar sesión: {}", e.getMessage(), e);
        }
    }
}