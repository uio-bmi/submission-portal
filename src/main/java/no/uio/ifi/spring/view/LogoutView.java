package no.uio.ifi.spring.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("logout")
public class LogoutView extends FormLayout {

    public LogoutView() {
        VaadinSession session = VaadinSession.getCurrent();
        UI ui = UI.getCurrent();

        session.setAttribute("username", null);
        ui.navigate(LoginView.class);
    }

}
