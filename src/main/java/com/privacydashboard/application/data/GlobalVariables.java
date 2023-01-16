package com.privacydashboard.application.data;

import java.util.Arrays;
import java.util.List;

public class GlobalVariables {
    public static final int nQuestions=30;
    public static final List<String> notificationType= Arrays.asList("Message", "Request", "PrivacyNotice");
    public static String pageTitle="";
    public enum RightType {
        WITHDRAWCONSENT, COMPLAIN, ERASURE, DELTEEVERYTHING, INFO, PORTABILITY
    }
    public enum Role {
        SUBJECT, CONTROLLER, DPO
    }
    public enum QuestionnaireVote {
        RED, ORANGE, GREEN
    }
}
