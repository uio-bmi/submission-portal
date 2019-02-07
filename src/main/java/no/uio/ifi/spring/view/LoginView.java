package no.uio.ifi.spring.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route
public class LoginView extends FlexLayout {


    private TextField username;
    private PasswordField password;
    private Button login;
    private Button forgotPassword;

    public LoginView() {
        buildUI();
        username.focus();
    }

    private void buildUI() {
        setSizeFull();
        setClassName("login-screen");

        Component loginForm = buildLoginForm();

        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);
        centeringLayout.add(loginForm);

        Component loginInformation = buildLoginInformation();

        add(loginInformation);
        add(centeringLayout);
    }

    private Component buildLoginForm() {
        FormLayout loginForm = new FormLayout();

        loginForm.setWidth("310px");

        loginForm.addFormItem(username = new TextField(), "Username");
        username.setWidth("15em");
        loginForm.add(new Html("<br/>"));
        loginForm.addFormItem(password = new PasswordField(), "Password");
        password.setWidth("15em");

        HorizontalLayout buttons = new HorizontalLayout();
        loginForm.add(new Html("<br/>"));
        loginForm.add(buttons);

        buttons.add(login = new Button("Login"));
        login.addClickListener(event -> login());
        loginForm.getElement().addEventListener("keypress", event -> login()).setFilter("event.key == 'Enter'");
        login.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        buttons.add(forgotPassword = new Button("Forgot password?"));
        forgotPassword.addClickListener(event -> showNotification(new Notification("Hint: password = username")));
        forgotPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        return loginForm;
    }

    private Component buildLoginInformation() {
        VerticalLayout loginInformation = new VerticalLayout();
        loginInformation.setClassName("login-information");

        H1 loginInfoHeader = new H1("Submission Portal");
        Image logo = new Image("https://ega-archive.org/blog/wp-content/uploads/2015/07/logo-ega.png", "EGA Submission Portal");
        Span span = new Span("The European Genome-phenome Archive (EGA) is designed to be a repository for all types of sequence and genotype experiments, including case-control, population, and family studies. We will include SNP and CNV genotypes from array based methods and genotyping done with re-sequencing methods. \n" +
                "\n" +
                "The EGA will serve as a permanent archive that will archive several levels of data including the raw data (which could, for example, be re-analysed in the future by other algorithms) as well as the genotype calls provided by the submitters. We are developing data mining and access tools for the database.\n" +
                "\n" +
                "For controlled access data, the EGA will provide the necessary security required to control access, and maintain patient confidentiality, while providing access to those researchers and clinicians authorised to view the data. In all cases, data access decisions will be made by the appropriate data access-granting organisation (DAO) and not by the EGA. The DAO will normally be the same organisation that approved and monitored the initial study protocol or a designate of this approving organisation.\n");
        loginInformation.add(logo);
        loginInformation.add(loginInfoHeader);
        loginInformation.add(span);

        return loginInformation;
    }

    private void login() {
        login.setEnabled(false);
        try {
            if (username.getOptionalValue().isPresent() && password.getOptionalValue().isPresent()) {
                if (username.getValue().equals(password.getValue())) {
                    VaadinSession.getCurrent().setAttribute("username", username.getValue());
                    UI.getCurrent().navigate(SubmissionView.class);
                } else {
                    showNotification(new Notification("Wrong username or password"));
                    username.focus();
                }
            } else {
                showNotification(new Notification("Username and password should be filled"));
                username.focus();
            }
        } finally {
            login.setEnabled(true);
        }
    }

    private void showNotification(Notification notification) {
        notification.setDuration(2000);
        notification.open();
    }

}
