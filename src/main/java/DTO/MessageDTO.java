package DTO;

import lombok.Getter;
import lombok.Setter;
import model.enumeration.EnumExecutedClass;
import model.enumeration.EnumOperationType;

import java.io.Serializable;

@Getter
@Setter
public class MessageDTO implements Serializable {
    private EnumExecutedClass executedClass;
    private EnumOperationType operationType;
    private Long userId;
    private Object object;
}
