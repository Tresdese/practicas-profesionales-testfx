package gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import java.math.BigDecimal;

public class CriterionRow {
    private final SimpleStringProperty criterionId;
    private final SimpleStringProperty criterionName;
    private final SimpleObjectProperty<BigDecimal> grade;
    private final SimpleStringProperty comments;

    public CriterionRow(String criterionId, String criterionName) {
        this.criterionId = new SimpleStringProperty(criterionId);
        this.criterionName = new SimpleStringProperty(criterionName);
        this.grade = new SimpleObjectProperty<>(null);
        this.comments = new SimpleStringProperty("");
    }

    public String getCriterionId() { return criterionId.get(); }
    public void setCriterionId(String value) { criterionId.set(value); }
    public SimpleStringProperty criterionIdProperty() { return criterionId; }

    public String getCriterionName() { return criterionName.get(); }
    public void setCriterionName(String value) { criterionName.set(value); }
    public SimpleStringProperty criterionNameProperty() { return criterionName; }

    public BigDecimal getGrade() { return grade.get(); }
    public void setGrade(BigDecimal value) { grade.set(value); }
    public SimpleObjectProperty<BigDecimal> gradeProperty() { return grade; }

    public String getComments() { return comments.get(); }
    public void setComments(String value) { comments.set(value); }
    public SimpleStringProperty commentsProperty() { return comments; }
}