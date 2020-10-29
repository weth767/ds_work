package DTO;

import lombok.Getter;
import lombok.Setter;
import model.enumeration.EnumOperationType;

import java.io.Serializable;

@Setter
@Getter
public class LogDTO implements Serializable {
    private EnumOperationType operationType;
    private Object operation;
    private String memberId;
}
