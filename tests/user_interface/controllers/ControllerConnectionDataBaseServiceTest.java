package user_interface.controllers;

import gui.GUI_RegisterDepartmentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.assertions.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ControllerConnectionDataBaseServiceTest extends ApplicationTest {
    // Para este test, es necesario que el servicio de base de datos esté desconectado.

    @Test
    public void loadRegisterProjectWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProject.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled())
                .hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterAcademicControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterAcademic.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertThat(lookup("#statusLabel").queryLabeled())
                .hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterActivityScheduleControllerWindowShowsAlertOnNoDatabase() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterActivitySchedule.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clickOn("#registerScheduleButton");
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(".alert", NodeMatchers.isVisible());
        verifyThat(".content", LabeledMatchers.hasText("Conexión interrumpida con la base de datos."));
    }

    @Test
    public void loadRegisterDepartmentControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterDepartment.fxml"));
                Parent root = loader.load();
                GUI_RegisterDepartmentController controller = loader.getController();
                controller.setOrganizationId(1);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        clickOn("#nameField").write("Departamento Test");
        clickOn("#descriptionArea").write("Descripción Test");

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#messageLabel").queryLabeled()).hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterGroupControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterGroup.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled())
                .hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterLinkedOrganizationControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterLinkedOrganization.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled())
                .hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterPeriodControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterPeriod.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        clickOn("#periodLabel").write("2025-1");
        clickOn("#nameField").write("Periodo de prueba");
        ((DatePicker) lookup("#startDateLabel").query()).setValue(java.time.LocalDate.of(2025, 1, 1));
        ((DatePicker) lookup("#endDateField").query()).setValue(java.time.LocalDate.of(2025, 6, 30));

        clickOn("#registerButton");

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled())
                .hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterPresentationControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterPresentation.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        interact(() -> {
            ComboBox<?> comboBox = lookup("#idProjectComboBox").query();
            comboBox.getSelectionModel().selectFirst();
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterProjectRequestControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterProjectRequest.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterRepresentativeControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterRepresentative.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterSelfAssessmentControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterSelfAssessment.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadRegisterStudentControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_RegisterStudent.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled()).hasText("Conexión interrumpida con la base de datos.");
    }

    @Test
    public void loadCheckAcademicListControllerWindowWithNoDataBaseService() {
        interact(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/GUI_CheckAcademicList.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        WaitForAsyncUtils.waitForFxEvents();

        assertThat(lookup("#statusLabel").queryLabeled())
                .hasText("Conexión interrumpida con la base de datos.");
    }
}
