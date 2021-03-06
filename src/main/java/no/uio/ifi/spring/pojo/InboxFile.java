package no.uio.ifi.spring.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@Data
public class InboxFile implements Serializable {

    private String path;
    private Long size;

}
