package no.uio.ifi.spring.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@Data
public class ErrorFile implements Serializable {

    private String file;
    private String reason;

}
