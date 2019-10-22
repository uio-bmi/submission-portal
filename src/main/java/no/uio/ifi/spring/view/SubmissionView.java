package no.uio.ifi.spring.view;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.spring.pojo.ArchiveFile;
import no.uio.ifi.spring.pojo.ErrorFile;
import no.uio.ifi.spring.pojo.InboxFile;
import no.uio.ifi.spring.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@Slf4j
@Push
@Route("")
@PWA(name = "EGA Submission Portal", shortName = "Portal")
public class SubmissionView extends AppLayout {

    private FilesService filesService;
    private String username;
    private Grid<InboxFile> inboxFileGrid = new Grid<>();
    private Grid<ArchiveFile> archiveFileGrid = new Grid<>();
    private Grid<ErrorFile> errorFileGrid = new Grid<>();

    public SubmissionView(@Autowired FilesService filesService, @Autowired Channel channel) {
        this.filesService = filesService;

        VaadinSession session = VaadinSession.getCurrent();
        if (session.getAttribute("username") == null) {
            return;
        }

        prepareMenu();

        username = session.getAttribute("username").toString();

        inboxFileGrid.addColumn(InboxFile::getPath).setHeader("Path");
        inboxFileGrid.addColumn(InboxFile::getSize).setHeader("Size");
        inboxFileGrid.setItems(filesService.getInboxFiles(username));

        archiveFileGrid.addColumn(ArchiveFile::getFile).setHeader("File");
        archiveFileGrid.addColumn(ArchiveFile::getId).setHeader("ID");
        archiveFileGrid.setItems(filesService.getArchiveFiles(username));

        errorFileGrid.addColumn(ErrorFile::getFile).setHeader("File");
        errorFileGrid.addColumn(ErrorFile::getReason).setHeader("Reason");
        errorFileGrid.setItems(filesService.getErrorFiles(username));

        Button submitButton = new Button("Submit");
        submitButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            for (InboxFile file : filesService.getInboxFiles(username)) {
                try {
                    String stableId = "EGAF" + UUID.randomUUID().toString().replace("-", "");
                    String message = String.format("{ \"user\": \"%s\", \"filepath\": \"%s\", \"stable_id\":\"%s\"}", username, file.getPath(), stableId);
                    log.info("Publishing message {}", message);
                    channel.basicPublish("localega.v1",
                            "files",
                            new AMQP.BasicProperties(),
                            message.getBytes()
                    );
                    filesService.clearInboxList(username);
                    Notification.show("Success!");
                } catch (IOException e) {
                    Notification.show(e.getMessage());
                }
            }
        });

        VerticalLayout inboxLayout = new VerticalLayout();
        inboxLayout.setSizeFull();
        inboxLayout.add(new H3("Inbox content for user: " + username), inboxFileGrid, submitButton);

        VerticalLayout archiveLayout = new VerticalLayout();
        archiveLayout.setSizeFull();
        archiveLayout.add(new H3("Archive files"), archiveFileGrid);

        Button clearButton = new Button("Clear all");
        clearButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> filesService.clearAll(username));

        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setSizeFull();
        errorLayout.add(new H3("Error files"), errorFileGrid, clearButton);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(inboxLayout, archiveLayout, errorLayout);

        setContent(horizontalLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                attachEvent.getUI().access(() -> {
                    inboxFileGrid.setItems(filesService.getInboxFiles(username));
                    archiveFileGrid.setItems(filesService.getArchiveFiles(username));
                    errorFileGrid.setItems(filesService.getErrorFiles(username));
                });
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 1000L;
        long period = 1000L;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    private void prepareMenu() {
        AppLayoutMenu menu = createMenu();
        Image logo = new Image("https://ega-archive.org/blog/wp-content/uploads/2015/07/logo-ega.png", "EGA Submission Portal");
        logo.setHeight("44px");
        setBranding(logo);
        menu.addMenuItems(new AppLayoutMenuItem("Logout", "logout"));
    }

}
