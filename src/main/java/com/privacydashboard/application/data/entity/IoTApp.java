package com.privacydashboard.application.data.entity;

import com.privacydashboard.application.data.QuestionnaireVote;

import javax.persistence.*;
import java.util.Dictionary;

@Entity
@Table(name= "iot_app")
public class IoTApp extends AbstractEntity{
    private String name;
    private String description;
    private QuestionnaireVote questionnaireVote;
    private String[] detailVote;
    //private Dictionary<Integer, String> optionalAnswers;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public QuestionnaireVote getQuestionnaireVote() {
        return questionnaireVote;
    }
    public void setQuestionnaireVote(QuestionnaireVote questionnaireVote) {
        this.questionnaireVote = questionnaireVote;
    }
    public String[] getDetailVote() {
        return detailVote;
    }
    public void setDetailVote(String[] detailVote) {
        this.detailVote = detailVote;
    }
    /*public Dictionary<Integer, String> getOptionalAnswers() {
        return optionalAnswers;
    }
    public void setOptionalAnswers(Dictionary<Integer, String> optionalAnswers) {
        this.optionalAnswers = optionalAnswers;
    }*/
}
