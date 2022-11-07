package com.privacydashboard.application.views.questionnaireDeveloper;

import com.privacydashboard.application.data.QuestionnaireVote;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.usefulComponents.MyDialog;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.*;

@PageTitle("Questionnaire")
@Route(value="questionnaire_developer")
@RolesAllowed({"CONTROLLER", "DPO"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class QuestionnaireDeveloper extends AppLayout implements BeforeEnterObserver, AfterNavigationObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;
    private IoTApp app;

    private final Tabs tabs= new Tabs();
    private final Span content= new Span();

    private final Integer nSections= 3;
    private final VerticalLayout[] sections= new VerticalLayout[nSections];

    private final Integer nQuestions= 29;
    private final Div[] titles= new Div[nQuestions];
    private final RadioButtonGroup<String>[] radioGroups= new RadioButtonGroup[nQuestions];
    private final Span[] icons= new Span[nQuestions];
    private final ContextMenu[] contextMenus= new ContextMenu[nQuestions];
    private final TextArea[] textAreas= new TextArea[nQuestions];
    private final VerticalLayout[] singleQuestion= new VerticalLayout[nQuestions];

    private Integer n;    // question number
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        app=communicationService.getApp();
    }

    public QuestionnaireDeveloper(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService){
        this.dataBaseService= dataBaseService;
        this.authenticatedUser= authenticatedUser;
        this.communicationService= communicationService;

        n=0;
        initializeLayout();
        section1(); // dati sensibili
        section2(); // sicurezza
        section3(); // test e certificazioni

        content.add(sections[0]);   // first section to be shown
    }

    private void initializeLayout(){
        for(int i=0; i<nQuestions; i++){
            icons[i]=new Span();
            icons[i].addClassNames("las la-info-circle");
            icons[i].addClassName("pointer");

            contextMenus[i]= new ContextMenu();
            contextMenus[i].setTarget(icons[i]);
            contextMenus[i].setOpenOnClick(true);
            contextMenus[i].addClassName("info");

            radioGroups[i]=new RadioButtonGroup<>();
            titles[i]= new Div();

            singleQuestion[i]=new VerticalLayout(titles[i], radioGroups[i]);
            singleQuestion[i].addClassName("singleQuestion-questionnaire");
        }

        Tab[] tab= new Tab[] {new Tab("Personal Data"), new Tab("Security"), new Tab("Tests and certifications")};
        for(int i=0; i<nSections; i++){
            sections[i]= new VerticalLayout();
            sections[i].addClassName("section-questionnaire");
            tab[i].addClassName("pointer");
            tabs.add(tab[i]);
        }
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(this::changeTab);
        setContent(content);
        setPrimarySection(Section.DRAWER);
    }

    // SEZIONE 1: DATI SENSIBILI
    private void section1(){
        int begin=n;

        // Hai individuato i dati che verranno processati e in che modo lo saranno?
        titles[n].add(new Span("Have you identified all the personal data that are going to be processed?"), icons[n]);
        contextMenus[n].addItem("According to the GDPR, personal data has to be processed in a particular and restricted way, so it it important to identify which are the personal dara that are going to be processed");
        radioGroups[n].setItems("Yes", "No", "I don't know");
        manageChangeColorQuestion(n, Collections.singletonList("Yes"), Collections.emptyList(), Arrays.asList("No", "I don't know"));
        n++;

        // Sono soltanto i dati strettamente necessari al funzionamento? (hidden question)
        titles[n].add(new Span("Are the personal data processed limited and used only for the purposes for which they are processed"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 5", "Personal data shall be: ... collected for specified, explicit and legitimate purposes and not further processed in a manner that is incompatible with those purposes ... adequate, relevant and limited to what is necessary in relation to the purposes for which they are processed"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        setHiddenQuestion(n, n-1, "Yes");
        n++;

        //Periodo mantenimento dei dati
        titles[n].add(new Span("For how long are the data going to be stored?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 13", "the controller shall, at the time when personal data are obtained, provide the data subject with the following further information... the period for which the personal data will be stored"));
        radioGroups[n].setItems("less than 1 month", "between 1 month and 6 months", "between 6 months and 2 years", "more than 2 years", "I don't know");
        n++;

        //Eliminazione dati
        titles[n].add(new Span("Do you have an automatic mechanism that deletes the personal data after the chosen period of time?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 13", "the controller shall, at the time when personal data are obtained, provide the data subject with the following further information... the period for which the personal data will be stored"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        //Does the app transfer data to a third party?
        titles[n].add(new Span("Does the app transfer data to a third party?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 13", "Where personal data relating to a data subject are collected from the data subject, the controller shall, at the time when personal data are obtained, provide the data subject with... the fact that the controller intends to transfer personal data to a third country or international organisation"));
        radioGroups[n].setItems("No", "Yes, only in European Union", "Yes, also outside of European Union", "I don't know");
        textAreas[n]= new TextArea("List which third countries, if any");
        n++;

        //Se usi server esterni, dove sono collocati?
        titles[n].add(new Span("If you use external servers, where are they located?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 13", "Where personal data relating to a data subject are collected from the data subject, the controller shall, at the time when personal data are obtained, provide the data subject with... the fact that the controller intends to transfer personal data to a third country or international organisation"));
        radioGroups[n].setItems("I don't use external servers", "They're located only in United Europe", "They're located also outside United Europe", "I don't know");
        n++;

        //Esistenza processo decisionale automatizzato
        titles[n].add(new Span("Is there an automated decision-making, including profiling?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 13", "the controller shall, at the time when personal data are obtained, provide the data subject with the following further information... the existence of automated decision-making, including profiling"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        //Dati facili da fornire a chi lo chiede
        titles[n].add(new Span("Are the data stored in a way that they're easily accessible to its legitimate owner when needed?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 12", "The controller shall facilitate the exercise of data subject rights"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        createSectionLayout(begin, n, 0);
        createButtonsLayout(false, true, false, 0);
    }

    // SEZIONE 2: SICUREZZA
    private void section2(){
        int begin=n;

        //Password
        titles[n].add(new Span("Do you store passwords in plain text or do you encrypt them?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("Plain text", "I encrypt them", "I don't store passwords", "I don't know");
        n++;

        //Which password hash (SHA-3, MD5, ...)
        titles[n].add(new Span("Which hash algorithm do you use to encrypt the passwords?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("SHA-3", "SHA-2", "SHA-1", "MD-5", "other");
        setHiddenQuestion(n, n-1, "I encrypt them");
        n++;

        //Which password constraints
        titles[n].add(new Span("Do you have any password constraints?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        textAreas[n]= new TextArea("List which constraints, if any");
        n++;

        // Confidentiality
        titles[n].add(new Span("Does your app guarantee confidentiality?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        textAreas[n]= new TextArea("How do you guarantee it?");
        n++;

        // Integrity
        titles[n].add(new Span("Does your app guarantee integrity?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        textAreas[n]= new TextArea("How do you guarantee it?");
        n++;

        // Usi pseudonomizzazione?
        titles[n].add(new Span("Do you pseudonymize the personal data?"));
        contextMenus[n].addItem(createInfo("GDPR Article 25", " the controller shall, both at the time of the determination of the means for processing and at the time of the processing itself, implement appropriate technical and organisational measures, such as pseudonymisation, which are designed to implement data-protection principles"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        // Usi cifratura?
        titles[n].add(new Span("Do you encrypt the personal data?"));
        contextMenus[n].addItem(createInfo("GDPR Article 32", "the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk, including inter alia as appropriate: ... encryption of personal data;"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        // Protocollo comunicazione
        titles[n].add(new Span("Which cryptographic protocol are you using for communication?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("TLS 1.2 or 1.3", "TLS < 1.2", "SSL", "My app doesn't need communication", "I don't use any", "I don't know");
        n++;

        // Porte
        titles[n].add(new Span("Do you limit the communication ports to the strictly necessary? (ex: only port 443 and 80 for https)"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("Yes", "No", "I don't know" );
        n++;

        //Backup
        titles[n].add(new Span("How often do you regularly make backups"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("every week", "between a week and a month", "between a month and a year", "more than a year", "never", "I don't know" );
        n++;

        // Procedure in caso di attacco?
        titles[n].add(new Span("Have you identified some procedures the availability and access to personal data in case of an incident?"));
        contextMenus[n].addItem(createInfo("GDPR Article 32", "the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk, including inter alia as appropriate: ... the ability to restore the availability and access to personal data in a timely manner in the event of a physical or technical incident;"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        createSectionLayout(begin, n, 1);
        createButtonsLayout(true, true, false, 1);
    }

    // SEZIONE 3: OPEN SOURCE
    private void section3(){
        int begin=n;

        // OpenChain
        titles[n].add(new Span("Did you follow OpenChain specification or other public specification for licensing compliance?"), icons[n]);
        contextMenus[n].addItem("It is easier to find if a software if compliant to the GDPR if it is open source");
        radioGroups[n].setItems("Yes, OpenChain", "Yes, other", "No", "I don't know");
        textAreas[n]= new TextArea("If other, which one?");
        n++;

        // PIA
        titles[n].add(new Span("Did you perform a Privacy Impact Assessment for at least a standard use case?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 35", " the controller shall, prior to the processing, carry out an assessment of the impact of the envisaged processing operations on the protection of personal data ... A data protection impact assessment referred to in paragraph 1 shall in particular be required in the case of:\n" +
                "\n" +
                "    a systematic and extensive evaluation of personal aspects \n" +
                "    processing on a large scale of special categories of data \n" +
                "    a systematic monitoring of a publicly accessible area on a large scale"));
        radioGroups[n].setItems("Yes", "No", "No, but it wasn't necessary", "I don't know");
        n++;

        // PIA
        titles[n].add(new Span("Is the Privacy Impact Assessment easily available at request?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 35", " the controller shall, prior to the processing, carry out an assessment of the impact of the envisaged processing operations on the protection of personal data ... A data protection impact assessment referred to in paragraph 1 shall in particular be required in the case of:\n" +
                "\n" +
                "    a systematic and extensive evaluation of personal aspects \n" +
                "    processing on a large scale of special categories of data \n" +
                "    a systematic monitoring of a publicly accessible area on a large scale"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        setHiddenQuestion(n, n-1, "Yes");
        n++;

        //Static analysis checks: coding style coding quality
        titles[n].add(new Span("Do you use any static analysis tool for code quality?"), icons[n]);
        contextMenus[n].addItem("Static analysis tools can help find vulnerabilities and reduce the complexity of the software");
        radioGroups[n].setItems("Yes", "No", "I don't know");
        textAreas[n]= new TextArea("If yes, which tools?");
        n++;

        // Certification
        titles[n].add(new Span("Do you have any certifications to demonstrate compliance with security requirements?"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 25", "An approved certification mechanism pursuant to Article 42 may be used as an element to demonstrate compliance with the requirements set out in paragraphs 1 and 2 of this Article"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        //Librerie terze
        titles[n].add(new Span("Do you use third party libraries?"), icons[n]);
        contextMenus[n].addItem("Third party libraries may reduce the security of the software, pay attention to what you include in your project");
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        //SE DOMANDA PRIMA ERA SI Domanda X: Librerie terze parte 2
        titles[n].add(new Span("Have you checked if these libraries comply with the current regulation about personal data?"), icons[n]);
        contextMenus[n].addItem("Third party libraries may reduce the security of the software, pay attention to what you include in your project");
        radioGroups[n].setItems("Yes", "No", "I don't know");
        setHiddenQuestion(n, n-1, "Yes");
        n++;

        // Test
        titles[n].add(new Span("Did you successfully perform any test on the software functionalities (unit test, integration test...)?"), icons[n]);
        contextMenus[n].addItem("It is important to test your app to prevent any unintentional and possibly dangerous behaviour");
        radioGroups[n].setItems("Yes", "No", "I don't know");
        textAreas[n]= new TextArea("If yes, which tests did you perform?");
        n++;

        // Code coverage
        titles[n].add(new Span("Did you perform a code coverage analysis of your test?"), icons[n]);
        contextMenus[n].addItem("It is important to test your app to prevent any unintentional and possibly dangerous behaviour");
        radioGroups[n].setItems("Yes, >=90%", "Yes, >=75% <90%", "Yes, >=50% <75%", "Yes, <50%", "No", "I don't know");
        setHiddenQuestion(n, n-1, "Yes");
        n++;

        // Test sicurezza?
        titles[n].add(new Span("Do you regularly test the effectiveness of your measures for ensuring the security of the process?"));
        contextMenus[n].addItem(createInfo("GDPR Article 32", "the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk, including inter alia as appropriate: ... a process for regularly testing, assessing and evaluating the effectiveness of technical and organisational measures for ensuring the security of the processing."));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        n++;

        createSectionLayout(begin, n, 2);
        createButtonsLayout(true, false, true, 2);
    }

    private void setHiddenQuestion(int question, int previousQuestion, String value){
        singleQuestion[question].setVisible(false);
        singleQuestion[question].addClassName("singleQuestionHidden-questionnaire");
        radioGroups[previousQuestion].addValueChangeListener(e-> singleQuestion[question].setVisible(value.equals(e.getValue())));
    }

    /*
    Each question has 3 different groups of answers: good answers(green), mediocre answers(orange) and bad answers(red).
    Based on the given answer, the section will change color accordingly
    */
    private void manageChangeColorQuestion(int question, List<String> green, List<String> orange, List<String> red){
        radioGroups[question].addValueChangeListener(e-> {
            singleQuestion[question].removeClassNames("green", "orange", "red");
            if(green.contains(radioGroups[question].getValue())){
                singleQuestion[question].addClassName("green");
                return;
            }
            if(orange.contains(radioGroups[question].getValue())){
                singleQuestion[question].addClassName("orange");
                return;
            }
            if(red.contains(radioGroups[question].getValue())){
                singleQuestion[question].addClassName("red");
            }
        });
    }

    private void createSectionLayout(int begin, int end, int section){
        for(int i=begin; i<end; i++){
            if(textAreas[i]!= null){
                textAreas[i].addClassName("textArea-questionnaire");
                singleQuestion[i].add(textAreas[i]);
            }
            sections[section].add(singleQuestion[i]);
        }
    }

    /*
    Left=true -> there is a previous page therefore there is a button that let you go in that page
    Right=true -> there is a next page therefore there is a button that let you go in that page
    End=true -> the last page, there is the 'Save' button
     */
    private void createButtonsLayout(boolean left, boolean right, boolean end, int section){
        HorizontalLayout layout= new HorizontalLayout();
        layout.addClassName("buttonLayout-questionnaire");

        Span leftButton= new Span();
        leftButton.addClassNames("las la-2x la-arrow-circle-left button-questionnaire");
        layout.add(leftButton);
        if(left){
            leftButton.addClassNames("activeButton-questionnaire pointer");
            leftButton.addClickListener(e-> tabs.setSelectedIndex(tabs.getSelectedIndex()-1));
        }

        if(end){
            Button saveButton= new Button("Save", e-> saveQuestionnaire());
            saveButton.addClassNames("saveButton-questionnaire");
            layout.add(saveButton);
        }

        Span rightButton = new Span();
        rightButton.addClassNames("las la-2x la-arrow-circle-right button-questionnaire");
        if(right){
            rightButton.addClassNames("activeButton-questionnaire pointer");
            rightButton.addClickListener(e-> tabs.setSelectedIndex(tabs.getSelectedIndex()+1));

        }
        layout.add(rightButton);

        sections[section].add(layout);
    }

    private void changeTab(Tabs.SelectedChangeEvent e){
        if(e.getSelectedTab().equals(e.getPreviousTab())){
            return;
        }
        content.removeAll();
        content.add(sections[tabs.getSelectedIndex()]);
    }

    private Html createInfo(String title, String description){
        return new Html("<p class=\"info\"><b>" + title +"</b>: <i>" + description + "</i></p>");
    }

    private void saveQuestionnaire(){
        MyDialog dialog= new MyDialog();
        dialog.setTitle("Confirm");
        dialog.setContent(new VerticalLayout(new Span("Are you sure you want to upload the questionnaire? Any previous one will be lost")));
        dialog.setContinueButton(new Button("Confirm", e-> {
            evaluateAndConfirm();
            dialog.close();
            UI.getCurrent().navigate(Questionnaire.class);
        }));
        dialog.open();
    }

    /*
    DA COMPLETARE
     */
    private void evaluateAndConfirm(){
        QuestionnaireVote vote= QuestionnaireVote.GREEN;
        String[] detailVote= new String[nQuestions];
        Hashtable<Integer, String> optionalAnswers= new Hashtable<>();
        for(int i=0; i<nQuestions; i++){
            /*
            VOTE:
            if there is a orange answer-> vote=orange
            if there is a red answer or a not given answer(not hidden question) -> vote=red
            otherwise -> vote=green
             */
            if(radioGroups[i].hasClassName("red")){
                vote= QuestionnaireVote.RED;
            }
            else if(radioGroups[i].hasClassName("orange") && !vote.equals(QuestionnaireVote.RED)){
                vote= QuestionnaireVote.ORANGE;
            }
            else if(!radioGroups[i].hasClassName("green") && radioGroups[i].isVisible()){ //Risposta non data e non è una domanda hidden?
                vote= QuestionnaireVote.RED;
            }

            //DetailVote
            detailVote[i]= radioGroups[i].getValue();

            //optional answers
            if(textAreas[i]!= null){
                optionalAnswers.put(i, textAreas[i].getValue());
            }
        }
        dataBaseService.updateQuestionnaireForApp(app, vote, detailVote, optionalAnswers);
        Notification notification = Notification.show("Questionnaire uploaded correctly");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Precompile questionnaire if there was a previous one
        if(app.getQuestionnaireVote()!=null && app.getDetailVote()!=null){
            for(int i=0; i<nQuestions; i++){
                radioGroups[i].setValue(app.getDetailVote()[i]);
                if(textAreas[i]!=null && app.getOptionalAnswers()!=null){
                    textAreas[i].setValue(app.getOptionalAnswers().get(i));
                }
            }
        }

        H1 title= new H1("Questionnaire " + app.getName());
        title.addClassName("title-questionnaire");
        com.vaadin.flow.component.html.Section sectionDrawer= new com.vaadin.flow.component.html.Section(title, tabs);
        sectionDrawer.addClassNames("drawer-questionnaire");
        addToDrawer(sectionDrawer);
    }

}