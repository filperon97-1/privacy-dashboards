package com.privacydashboard.application.views.questionnaireDeveloper;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.security.PermitAll;

@PageTitle("Questionnaire")
@Route(value="questionnaire_developer")
@PermitAll
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class QuestionnaireDeveloper extends AppLayout {
    private final Tabs tabs= new Tabs();
    private final Integer nSections= 4;
    private final VerticalLayout[] sections= new VerticalLayout[nSections];
    private final Integer nQuestions= 30;
    private final Div[] titles= new Div[nQuestions];
    private final RadioButtonGroup<String>[] radioGroups= new RadioButtonGroup[nQuestions];
    private final Span[] icons= new Span[nQuestions];
    private final ContextMenu[] contextMenus= new ContextMenu[nQuestions];
    private final TextArea[] textAreas= new TextArea[nQuestions];
    private final VerticalLayout[] singleQuestion= new VerticalLayout[nQuestions];
    private final Span content= new Span();

    private Integer n=0;    // question number

    Logger logger = LoggerFactory.getLogger(getClass());
    public QuestionnaireDeveloper(){
        initializeLayout();
        section1(); // dati sensibili
        section2(); // sicurezza
        section3(); // open source
        section4(); // licenze, tools e test

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

        for(int i=0; i<nSections; i++){
            sections[i]= new VerticalLayout();
            sections[i].addClassName("section-questionnaire");
            Tab tab=new Tab("Section " + (i+1));
            tab.addClassName("pointer");
            tabs.add(tab);
        }
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addSelectedChangeListener(this::changeTab);

        H1 title= new H1("Questionnaire");
        title.addClassName("title-questionnaire");

        com.vaadin.flow.component.html.Section sectionDrawer= new com.vaadin.flow.component.html.Section(title, tabs);
        sectionDrawer.addClassNames("drawer-questionnaire");
        addToDrawer(sectionDrawer);
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
        n++;

        // Sono soltanto i dati strettamente necessari al funzionamento? (hidden question)
        titles[n].add(new Span("Are the personal data processed limited and used only for the purposes for which they are processed"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 5", "Personal data shall be: ... collected for specified, explicit and legitimate purposes and not further processed in a manner that is incompatible with those purposes ... adequate, relevant and limited to what is necessary in relation to the purposes for which they are processed"));
        radioGroups[n].setItems("Yes", "No", "I don't know");
        setHiddenQuestion(n, n-1, "Yes");
        n++;

        // Pseudonominizzazione
        titles[n].add(new Span("Are the personal data processed pseudonymized or anonymized?"), icons[n]);
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
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems();
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

        //Domanda X: Backup
        titles[n].add(new Span("How often do you regularly make backups"), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems("every week", "between a week and a month", "between a month and a year", "more than a year", "never", "I don't know" );
        n++;

        /*
        In terms of database management, good practices include:

    using nominative accounts for database access and create specific accounts for each application;
    revoking the administrative privileges of user or application accounts to avoid modification to database structure (table, vues, process, etc);
    having protection against SQL or script injection attacks;
    encouraging at rest disk and database encryption.

         */

        createSectionLayout(begin, n, 1);
        createButtonsLayout(true, true, false, 1);
    }

    // SEZIONE 3: OPEN SOURCE
    private void section3(){

        // D2.6

        /*
           ARTICOLO 25: misure tecniche e organizzative adeguate, quali la pseudonimizzazione, volte ad attuare in modo efficace i principi di protezione dei dati, quali la minimizzazione
                         misure tecniche e organizzative adeguate per garantire che siano trattati, per impostazione predefinita, solo i dati personali necessari
           ARTIOLO 32: misure tecniche e organizzative adeguate per garantire un livello di sicurezza adeguato al rischio
                        esempi: pseudonimizzazione e la cifratura dei dati personali;
                                 ripristinare tempestivamente la disponibilità e l'accesso dei dati personali in caso di incidente fisico o tecnico;
                                 procedura per testare, verificare e valutare regolarmente l'efficacia delle misure tecniche e organizzative al fine di garantire la sicurezza del trattamento.

         */

        // CONSIGLI CNIL
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
        /*
        //
        titles[n].add(new Span(""), icons[n]);
        contextMenus[n].addItem(createInfo("GDPR Article 32", " the controller and the processor shall implement appropriate technical and organisational measures to ensure a level of security appropriate to the risk"));
        radioGroups[n].setItems();
        n++;
         */

        createSectionLayout(begin, n, 2);
        createButtonsLayout(true, true, false, 2);
    }


    // SEZIONE 4: LICENZE
    private void section4(){
        int begin=n;
        // Domande sparse

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
        titles[n].add(new Span("Did you successfully perform any test on the software functionalities (unit test, integration test...)"), icons[n]);
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

        createSectionLayout(begin, n, 3);
        createButtonsLayout(true, false, true, 3);
    }

    private void setHiddenQuestion(int question, int previousQuestion, String value){
        singleQuestion[question].setVisible(false);
        singleQuestion[question].addClassName("singleQuestionHidden-questionnaire");
        radioGroups[previousQuestion].addValueChangeListener(e-> singleQuestion[question].setVisible(value.equals(e.getValue())));
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

    }
}