package no.uio.ifi.spring.service;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.spring.pojo.ArchiveFile;
import no.uio.ifi.spring.pojo.ErrorFile;
import no.uio.ifi.spring.pojo.InboxFile;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class FilesService {

    private final Gson gson = new Gson();
    private final ConcurrentMap inboxFiles;
    private final ConcurrentMap archiveFiles;
    private final ConcurrentMap errorFiles;

    @Autowired
    private Channel channel;

    public FilesService() {
        DB db = DBMaker.fileDB("ega.db").closeOnJvmShutdown().make();
        inboxFiles = db.hashMap("inboxFiles").createOrOpen();
        archiveFiles = db.hashMap("archiveFiles").createOrOpen();
        errorFiles = db.hashMap("errorFiles").createOrOpen();
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() throws IOException {
        DeliverCallback inboxCallback = (consumerTag, delivery) -> {
            String body = new String(delivery.getBody(), Charset.defaultCharset());
            log.info("Received v1.files.inbox message: {}", body);
            Map message = gson.fromJson(body, Map.class);
            String key = message.get("user").toString();
            HashSet<InboxFile> files = (HashSet<InboxFile>) inboxFiles.computeIfAbsent(key, k -> new HashSet<>());
            files.add(new InboxFile(message.get("filepath").toString(), (long) Double.parseDouble(message.get("filesize").toString())));
            inboxFiles.put(key, files);
        };
        channel.basicConsume("v1.files.inbox", true, inboxCallback, consumerTag -> {
        });

        DeliverCallback archiveCallback = (consumerTag, delivery) -> {
            String body = new String(delivery.getBody(), Charset.defaultCharset());
            log.info("Received v1.files.completed message: {}", body);
            Map message = gson.fromJson(body, Map.class);
            String key = message.get("user").toString();
            HashSet<ArchiveFile> files = (HashSet<ArchiveFile>) archiveFiles.computeIfAbsent(key, k -> new HashSet<>());
            files.add(new ArchiveFile(message.get("filepath").toString(), (long) Double.parseDouble(message.get("reference").toString())));
            archiveFiles.put(key, files);
        };
        channel.basicConsume("v1.files.completed", true, archiveCallback, consumerTag -> {
        });

        DeliverCallback errorCallback = (consumerTag, delivery) -> {
            String body = new String(delivery.getBody(), Charset.defaultCharset());
            log.info("Received v1.files.error message: {}", body);
            Map message = gson.fromJson(body, Map.class);
            String key = message.get("user").toString();
            HashSet<ErrorFile> files = (HashSet<ErrorFile>) errorFiles.computeIfAbsent(key, k -> new HashSet<>());
            files.add(new ErrorFile(message.get("filepath").toString(), message.get("reason").toString()));
            errorFiles.put(key, files);
        };
        channel.basicConsume("v1.files.error", true, errorCallback, consumerTag -> {
        });
    }

    @SuppressWarnings("unchecked")
    public Collection<InboxFile> getInboxFiles(String username) {
        return (Collection<InboxFile>) inboxFiles.computeIfAbsent(username, k -> new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    public Collection<ArchiveFile> getArchiveFiles(String username) {
        return (Collection<ArchiveFile>) archiveFiles.computeIfAbsent(username, k -> new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    public Collection<ErrorFile> getErrorFiles(String username) {
        return (Collection<ErrorFile>) errorFiles.computeIfAbsent(username, k -> new HashSet<>());
    }

    public void clear(String username) {
        inboxFiles.remove(username);
    }

}
