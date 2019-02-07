package no.uio.ifi.spring.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import no.uio.ifi.spring.pojo.InboxFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Service
public class FilesService {

    private final Gson gson = new Gson();
    private final Multimap<String, InboxFile> files = HashMultimap.create();

    @Autowired
    private Channel channel;

    @PostConstruct
    public void init() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String body = new String(delivery.getBody(), Charset.defaultCharset());
            log.info("Received message: {}", body);
            Map message = gson.fromJson(body, Map.class);
            synchronized (files) {
                files.put(message.get("user").toString(), new InboxFile(message.get("filepath").toString(), (long) Double.parseDouble(message.get("filesize").toString())));
            }
        };
        channel.basicConsume("v1.files.inbox", true, deliverCallback, consumerTag -> {
        });
    }

    public Collection<InboxFile> getFiles(String username) {
        synchronized (files) {
            return files.get(username);
        }
    }

    public void clear(String username) {
        synchronized (files) {
            files.removeAll(username);
        }
    }

}
