package no.uio.ifi.spring.listener;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import no.uio.ifi.spring.view.LoginView;

public class AuthenticationVaadinServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent initEvent) {
        initEvent.getSource().addUIInitListener(uiInitEvent -> uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
            if (VaadinSession.getCurrent().getAttribute("username") == null && !LoginView.class
                    .equals(enterEvent.getNavigationTarget()))
                enterEvent.rerouteTo(LoginView.class);
        }));
    }

}
