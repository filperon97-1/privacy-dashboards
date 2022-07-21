package com.privacydashboard.application.views;

import com.privacydashboard.application.data.entity.Notification;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.applyRights.RightsView;
import com.privacydashboard.application.views.apps.AppsView;
import com.privacydashboard.application.views.home.HomeView;
import com.privacydashboard.application.views.contacts.ContactsView;
import com.privacydashboard.application.views.messages.MessagesView;
import com.privacydashboard.application.views.messages.SingleConversationView;
import com.privacydashboard.application.views.rightRequest.RightRequestsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class MainLayout extends AppLayout {
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view);

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }
        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {
            public LineAwesomeIcon(String lineawesomeClassnames) {
                addClassNames("menu-item-icon");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }
        }

    }

    // the text will change based on the current page(afterNavigation())
    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    private DataBaseService dataBaseService;
    private ContextMenu menuNotifications;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, DataBaseService dataBaseService) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.dataBaseService= dataBaseService;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");
        Icon bellIcon= initializeNotifications();

        Header header = new Header(toggle, viewTitle, bellIcon);
        header.addClassNames("view-header");
        return header;
    }

    private Icon initializeNotifications(){
        Icon bellIcon= new Icon(VaadinIcon.BELL_O);
        bellIcon.addClassNames("bell-icon");
        menuNotifications=new ContextMenu(bellIcon);
        menuNotifications.setOpenOnClick(true);
        updateNotifications();
        //menuNotifications.addOpenedChangeListener(e-> showNotifications());

        //menu.addItem(showNotifications());
        return bellIcon;
    }

    private void updateNotifications(){
        menuNotifications.removeAll();
        Optional<User> maybeUser = authenticatedUser.get();
        if(maybeUser.isEmpty()){
            return;
        }
        List<Notification> notifications=dataBaseService.getNewNotificationsFromUser(maybeUser.get());
        for(Notification notification  : notifications){
            menuNotifications.addItem(notification.getDescription(), e-> goToNotification(notification));
        }
    }

    // DA IMPLEMENTARE: IN CASO NOTIFICATION SIA PER RIGHT REQUEST
    private void goToNotification(Notification notification){
        // Message notification
        if(notification.getMessage()!=null && notification.getRequest()==null){
            dataBaseService.changeIsReadNotification(notification, true);
            updateNotifications();
            UI.getCurrent().navigate(SingleConversationView.class, new RouteParameters("contactID", notification.getSender().getId().toString()));
        }
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Privacy Dashboard");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }

        }
        return nav;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("Home", "la la-file", HomeView.class), //

                new MenuItemInfo("Contacts", "las la-address-book", ContactsView.class), //

                new MenuItemInfo("Messages", "las la-comments", MessagesView.class), //

                new MenuItemInfo("Rights", "las la-school", RightsView.class),  //ONLY FOR SUBJECTS

                new MenuItemInfo("Rights", "las la-school", RightRequestsView.class),  //ONLY FOR CONTROLLERS AND DPOS

                new MenuItemInfo("Apps", "la la-list", AppsView.class), //

        };
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName(), user.getProfilePictureUrl());
            avatar.addClassNames("me-xs");

            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Logout", e -> {
                authenticatedUser.logout();
            });

            Span name = new Span(user.getName());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, name);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }



    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
