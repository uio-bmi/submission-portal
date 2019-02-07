package no.uio.ifi.spring.service;

import no.uio.ifi.spring.pojo.InboxFile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@Service
public class FilesService {

    public Collection<InboxFile> getFiles(String username) {
        return Arrays.asList(new InboxFile("test.enc", new Random().nextLong()), new InboxFile("sub/test2.enc", new Random().nextLong()));
    }

}
