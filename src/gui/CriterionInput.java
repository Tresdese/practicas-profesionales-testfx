package gui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;

public class CriterionInput {
    private static final int MAX_COMMENTS_LENGTH = 50;

    String idCriteria;
    Label nameLabel;
    TextField gradeField;
    TextField commentsField;
    Label commentsCharCountLabel;

    public CriterionInput(String idCriteria, String name) {
        this.idCriteria = idCriteria;
        this.nameLabel = new Label(name);
        this.gradeField = new TextField();
        this.gradeField.setPromptText("Calificación");
        this.commentsField = new TextField();
        this.commentsField.setPromptText("Comentarios");
        this.commentsCharCountLabel = new Label("0/" + MAX_COMMENTS_LENGTH);

        configureCommentsCharCount();
    }

    private void configureCommentsCharCount() {
        commentsField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > MAX_COMMENTS_LENGTH) {
                change.setText(newText.substring(change.getRangeStart(), change.getRangeStart() + MAX_COMMENTS_LENGTH - change.getControlText().length()));
                change.setAnchor(MAX_COMMENTS_LENGTH);
                change.setCaretPosition(MAX_COMMENTS_LENGTH);
            }
            return change;
        }));

        commentsField.textProperty().addListener((observable, oldText, newText) -> {
            commentsCharCountLabel.setText(Math.min(newText.length(), MAX_COMMENTS_LENGTH) + "/" + MAX_COMMENTS_LENGTH);
        });
    }

    public HBox toHBox() {
        HBox hbox = new HBox(12, nameLabel, gradeField, commentsField, commentsCharCountLabel);
        hbox.setStyle("-fx-padding: 4 0 4 0;");
        return hbox;
    }

    public String getGrade() {
        return gradeField.getText();
    }

    public String getComments() {
        return commentsField.getText();
    }
}