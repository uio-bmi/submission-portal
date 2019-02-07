package no.uio.ifi.spring.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
@Data
public class InboxFile {

    private String path;
    private Long size;

}
