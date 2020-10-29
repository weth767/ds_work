package model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Log implements Serializable {
    private Long id;
    private String operation;
    private String memberId;
}
