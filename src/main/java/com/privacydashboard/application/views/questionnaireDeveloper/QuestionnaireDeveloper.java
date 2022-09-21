package com.privacydashboard.application.views.questionnaireDeveloper;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PageTitle("Questionnaire")
@Route(value="questionnaire_developer")
@PermitAll
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class QuestionnaireDeveloper extends AppLayout {
    private final Integer nQuestions=3;
    private final Div[] titles= new Div[nQuestions];
    private final RadioButtonGroup<String>[] radioGroups= new RadioButtonGroup[nQuestions];
    private final Span[] icons= new Span[nQuestions];
    private final ContextMenu[] contextMenus= new ContextMenu[nQuestions];
    public QuestionnaireDeveloper(){

        // STILE DELLA PAGINA: DA PENSARCI DOPO AVER INDIVIDUATO TUTTE LE DOMANDE DEL QUESTIONARIO E IN CASO AVERLE DIVISE
        // IN SEIZIONI
        /*
        H1 title= new H1("Questionnaire");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)");
        Tabs tabs= new Tabs(new Tab("Section 1"), new Tab("Section 2"));
        tabs.addClassName("tabs");
        addToNavbar(title,tabs);
        addToDrawer(new VerticalLayout(new Span("AAAAAA"), new Span("AAAAAA"), new Span("AAAAAA"), new Span("AAAAAA")));
        //setResponsiveSteps(new ResponsiveStep("0", 1));
         */

        // associate icons with context menu
        for(int i=0; i<nQuestions; i++){
            icons[i]=new Span();
            icons[i].addClassNames("las la-info-circle");
            icons[i].addClassName("pointer");

            contextMenus[i]= new ContextMenu();
            contextMenus[i].setTarget(icons[i]);
            contextMenus[i].setOpenOnClick(true);
            contextMenus[i].addClassName("info");
        }

        // SEZIONE 1

        //Domanda 1: Does the app transfer data to a third party?
        titles[0]= new Div(new Span("Does the app transfer data to a third party?"), icons[0]);
        contextMenus[0].addItem("GDPR Article 13: Where personal data relating to a data subject are collected from the data subject, the controller shall, at the time when personal data are obtained, provide the data subject with... the fact that the controller intends to transfer personal data to a third country or international organisation ");
        radioGroups[0]= new RadioButtonGroup<>();
        radioGroups[0].setItems("No", "Yes, only in United Europe", "Yes, also outside United Europe", "I don't know");

        //Domanda 2: Periodo mantenimento dei dati
        titles[1]= new Div(new Span("For how long are the data going to be stored?"), icons[1]);
        contextMenus[1].addItem("GDPR Article 13: the controller shall, at the time when personal data are obtained, provide the data subject with the following further information... the period for which the personal data will be stored");
        radioGroups[1]= new RadioButtonGroup<>();
        radioGroups[1].setItems("less than 1 month", "between 1 month and 6 months", "between 6 months and 2 years", "more than 2 years", "I don't know");

        //Domanda 3: Esistenza processo decisionale automatizzato
        titles[2]= new Div(new Span("Is there an automated decision-making, including profiling?"), icons[2]);
        contextMenus[2].addItem("GDPR Article 13: the controller shall, at the time when personal data are obtained, provide the data subject with the following further information... the existence of automated decision-making, including profiling");
        radioGroups[2]= new RadioButtonGroup<>();
        radioGroups[2].setItems("Yes", "No", "I don't know");

        //Domanda 4: Che dati vengono processati?

        //Domanda X: Hai individuato i dati che verranno processati e in che modo lo saranno?
        /*
        titles[]= new Div(new Span("Have you identified all the data that are going to be processed?"), icons[]);
        contextMenus[].addItem("");
        radioGroups[]= new RadioButtonGroup<>();
        radioGroups[].setItems("Yes", "No", "I don't know");
         */

        //Domanda X: Se usi server esterni, dove sono collocati?
        /*
        titles[]= new Div(new Span("If you use external servers, where are they located?"), icons[]);
        contextMenus[].addItem("");
        radioGroups[]= new RadioButtonGroup<>();
        radioGroups[].setItems("I don't use external servers", "They're located only in United Europe", "They're located also outside United Europe", "I don't know");
        */

        //Domanda X: Dati facili da fornire a chi lo chiede
        /*
        titles[]= new Div(new Span("Are the data stored in a way that they're easily accessible to its legitimate owner when needed?"), icons[]);
        contextMenus[].addItem("");
        radioGroups[]= new RadioButtonGroup<>();
        radioGroups[].setItems("Yes", "No", "I don't know");
        */

        //SEZIONE 2

        // Domanda 1: Usi pseudonomizzazione?

        //Domanda 2: App utilizza solo i dati necessari?

        //SEZIONE 3

        //Domanda 1: Cifratura?

        //Domanda 2: Sono stati fatti dei test per verificare le procedure di sicurezza?

        //Domanda X:
        /*
        titles[]= new Div(new Span(""), icons[]);
        contextMenus[].addItem("");
        radioGroups[]= new RadioButtonGroup<>();
        radioGroups[].setItems();
        */

        VerticalLayout internalLayout= new VerticalLayout();
        for(int i=0; i<nQuestions; i++){
            internalLayout.add(titles[i], radioGroups[i]);
        }
        setContent(internalLayout);
    }
}

/*CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("Does the app transfer data to a third party?");
        checkboxGroup.setItems("No", "Yes, in United Europe", "Yes, outside United Europe", "I don't know");

        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel("Does the app transfer data to a third party?");
        radioGroup.setItems("No", "Yes, in United Europe", "Yes, outside United Europe", "I don't know");*/
