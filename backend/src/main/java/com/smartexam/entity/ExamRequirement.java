
package com.smartexam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_requirements")
public class ExamRequirement {

    @Id
    @Column(name = "requirement_id", length = 30)
    private String requirementId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "requirement_type", nullable = false)
    private String requirementType;

    @Column(name = "requirement_value", nullable = false)
    private String requirementValue;

    @Column(nullable = false)
    private boolean mandatory = true;

    @Column(length = 500)
    private String description;

    public ExamRequirement() {
    }

    public String getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(String requirementId) {
        this.requirementId = requirementId;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public String getRequirementValue() {
        return requirementValue;
    }

    public void setRequirementValue(String requirementValue) {
        this.requirementValue = requirementValue;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
