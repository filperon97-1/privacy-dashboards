package com.privacydashboard.application.views.questionnaireDeveloper;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.splitlayout.SplitLayout;
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
    private final Integer nSections= 4;
    private final VerticalLayout[] sections= new VerticalLayout[nSections];
    private final Integer nQuestions= 13;
    private final Div[] titles= new Div[nQuestions];
    private final RadioButtonGroup<String>[] radioGroups= new RadioButtonGroup[nQuestions];
    private final Span[] icons= new Span[nQuestions];
    private final ContextMenu[] contextMenus= new ContextMenu[nQuestions];
    private final TextArea[] textAreas= new TextArea[nQuestions];
    private final VerticalLayout[] singleQuestion= new VerticalLayout[nQuestions];

    Logger logger = LoggerFactory.getLogger(getClass());
    public QuestionnaireDeveloper(){


        // STILE DELLA PAGINA: DA PENSARCI DOPO AVER INDIVIDUATO TUTTE LE DOMANDE DEL QUESTIONARIO E IN CASO AVERLE DIVISE
        // IN SEZIONI

        setPrimarySection(Section.DRAWER);
        H1 title= new H1("Questionnaire");
        title.addClassName("title-questionnaire");
        Tabs tabs= new Tabs();
        for(int i=0; i<nSections; i++){
            Tab tab=new Tab("Section " + Integer.toString(i+1));
            tab.addClassName("pointer");
            tabs.add(tab);
            sections[i]= new VerticalLayout();
            sections[i].addClassName("section-questionnaire");
        }
        /*Tabs tabs= new Tabs(new Tab("Section 1"), new Tab("Section 2"), new Tab("Section 3"), new Tab("Section 4"));
        for(int i=0; i<tabs.getComponentCount(); i++){
            tabs.setSelectedIndex(i);
            tabs.getSelectedTab().addClassName("pointer");
        }*/
        //tabs.setSelectedIndex(0);
        //tabs.addClassName("tabs");
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        //addToNavbar(title,tabs);
        com.vaadin.flow.component.html.Section sectionDrawer= new com.vaadin.flow.component.html.Section(title, tabs);
        addToDrawer(sectionDrawer);
        //addToDrawer(new VerticalLayout(new Span("AAAAAA"), new Span("AAAAAA"), new Span("AAAAAA"), new Span("AAAAAA")));
        //setResponsiveSteps(new ResponsiveStep("0", 1));
        sectionDrawer.addClassNames("drawer-questionnaire");

        // FINE PARTE SULLO STILE

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
        int k=0;

        //Domanda 1: Does the app transfer data to a third party?
        titles[0]= new Div(new Span("Does the app transfer data to a third party?"), icons[0]);
        contextMenus[0].addItem(createInfo("GDPR Article 13", "Where personal data relating to a data subject are collected from the data subject, the controller shall, at the time when personal data are obtained, provide the data subject with... the fact that the controller intends to transfer personal data to a third country or international organisation"));
        radioGroups[0]= new RadioButtonGroup<>();
        radioGroups[0].setItems("No", "Yes, only in United Europe", "Yes, also outside United Europe", "I don't know");
        textAreas[0]= new TextArea("List which third countries, if any");


        //Domanda 2: Periodo mantenimento dei dati
        titles[1]= new Div(new Span("For how long are the data going to be stored?"), icons[1]);
        contextMenus[1].addItem(createInfo("GDPR Article 13", "the controller shall, at the time when personal data are obtained, provide the data subject with the following further information... the period for which the personal data will be stored"));
        radioGroups[1]= new RadioButtonGroup<>();
        radioGroups[1].setItems("less than 1 month", "between 1 month and 6 months", "between 6 months and 2 years", "more than 2 years", "I don't know");

        //Domanda 3: Esistenza processo decisionale automatizzato
        titles[2]= new Div(new Span("Is there an automated decision-making, including profiling?"), icons[2]);
        contextMenus[2].addItem(createInfo("GDPR Article 13", "the controller shall, at the time when personal data are obtained, provide the data subject with the following further information... the existence of automated decision-making, including profiling"));
        radioGroups[2]= new RadioButtonGroup<>();
        radioGroups[2].setItems("Yes", "No", "I don't know");

        //Domanda 4: Che dati vengono processati?

        k=3;
        //Domanda X: Hai individuato i dati che verranno processati e in che modo lo saranno?
        titles[k]= new Div(new Span("Have you identified all the data that are going to be processed?"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("Yes", "No", "I don't know");
        k++;

        //Domanda X: Se usi server esterni, dove sono collocati?
        titles[k]= new Div(new Span("If you use external servers, where are they located?"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("I don't use external servers", "They're located only in United Europe", "They're located also outside United Europe", "I don't know");
        k++;

        //Domanda X: Dati facili da fornire a chi lo chiede
        titles[k]= new Div(new Span("Are the data stored in a way that they're easily accessible to its legitimate owner when needed?"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("Yes", "No", "I don't know");
        k++;

        //Domanda X: Eliminazione dati
        titles[k]= new Div(new Span("Do you have an automatic mechanism that deletes the personal data after the chose period of time?"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("Yes", "No", "I don't know");
        k++;

        //Domanda X: Librerie terze
        titles[k]= new Div(new Span("Do you use third party libraries?"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("Yes", "No", "I don't know");
        k++;

        //SE DOMANDA PRIMA ERA SI Domanda X: Librerie terze parte 2
        titles[k]= new Div(new Span("Have you checked if these libraries comply with the current regulation about personal data?"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("Yes", "No", "I don't know");
        k++;

        //SEZIONE 2

        // Domanda 1: Usi pseudonomizzazione?

        //Domanda 2: App utilizza solo i dati necessari?

        //SEZIONE 3 SICUREZZA

        //Domanda 1: Cifratura?

        //Domanda X:Password
        titles[k]= new Div(new Span("Do you store passwords in plain text or do you encrypt them?"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("Plain text", "I encrypt them", "I don't store passwords", "I don't know");
        k++;

        //Domanda X: Protocollo comunicazione
        titles[k]= new Div(new Span("Which cryptographic protocol are you using for communication?"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("TLS 1.2 or 1.3", "TLS < 1.2", "SSL", "My app doesn't need communication", "I don't use any", "I don't know");
        k++;

        //Domanda 2: Sono stati fatti dei test per verificare le procedure di sicurezza?

        //Domanda X: Backup
        titles[k]= new Div(new Span("How often do you regularly make backups"), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems("every week", "between a week and a month", "between a month and a year", "more than a year", "never", "I don't know" );
        k++;

        //Domanda X:
        titles[k]= new Div(new Span(""), icons[k]);
        contextMenus[k].addItem("");
        radioGroups[k]= new RadioButtonGroup<>();
        radioGroups[k].setItems();
        k++;

        VerticalLayout internalLayout= new VerticalLayout();
        internalLayout.addClassName("internalLayout-questionnaire");
        for(int i=0; i<nQuestions; i++){
            singleQuestion[i]=new VerticalLayout(titles[i], radioGroups[i]);
            singleQuestion[i].addClassName("singleQuestion-questionnaire");
            if(textAreas[i]!= null){
                textAreas[i].addClassName("textArea-questionnaire");
                singleQuestion[i].add(textAreas[i]);
            }
            internalLayout.add(singleQuestion[i]);
        }
        setContent(internalLayout);
    }

    private Html createInfo(String title, String description){
        return new Html("<p class=\"info\"><b>" + title +"</b>: <i>" + description + "</i></p>");
    }
}