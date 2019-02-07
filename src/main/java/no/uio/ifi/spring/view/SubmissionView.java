package no.uio.ifi.spring.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import no.uio.ifi.spring.pojo.InboxFile;
import no.uio.ifi.spring.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Timer;
import java.util.TimerTask;

@Push
@Route
@PWA(name = "EGA Submission Portal", shortName = "Portal")
public class SubmissionView extends AppLayout {

    private FilesService filesService;
    private String username;
    private Grid<InboxFile> grid = new Grid<>();

    public SubmissionView(@Autowired FilesService filesService) {
        this.filesService = filesService;

        VaadinSession session = VaadinSession.getCurrent();
        if (session.getAttribute("username") == null) {
            return;
        }

        prepareMenu();

        username = session.getAttribute("username").toString();

        grid.addColumn(InboxFile::getPath).setHeader("Path");
        grid.addColumn(InboxFile::getSize).setHeader("Size");

        grid.setItems(filesService.getFiles(username));

        Component content = new Span(new H3("Inbox content for " + username), grid);

        setContent(content);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                attachEvent.getUI().access(() -> grid.setItems(filesService.getFiles(username)));
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 1000L;
        long period = 1000L;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private void prepareMenu() {
        AppLayoutMenu menu = createMenu();
        Image img = new Image("https://ega-archive.org/blog/wp-content/uploads/2015/07/logo-ega.png", "EGA Submission Portal");
        img.setHeight("44px");
        setBranding(img);
        menu.addMenuItems(new AppLayoutMenuItem("Logout", "logout"));
    }

}
