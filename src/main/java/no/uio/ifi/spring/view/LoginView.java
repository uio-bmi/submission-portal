package no.uio.ifi.spring.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route
public class LoginView extends FormLayout {

    public LoginView() {
        VaadinSession session = VaadinSession.getCurrent();
        UI ui = UI.getCurrent();

        TextField login = new TextField();
        login.setPlaceholder("john");
        addFormItem(login, "Username");

        PasswordField password = new PasswordField();
        password.setPlaceholder("password");
        addFormItem(password, "Password");

        Button loginButton = new Button("Login");
        loginButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (login.getOptionalValue().isPresent() && password.getOptionalValue().isPresent()) {
                if (login.getValue().equals(password.getValue())) {
                    session.setAttribute("username", login.getValue());
                    ui.navigate(SubmissionView.class);
                } else {
                    Notification.show("Wrong password");
                }
            } else {
                Notification.show("Username and password should be filled");
            }
        });

        addFormItem(loginButton, "Login");
    }

}
