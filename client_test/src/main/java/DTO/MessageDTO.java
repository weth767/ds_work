package DTO;

import lombok.Getter;
import lombok.Setter;
import model.enumeration.EnumBankMessages;

import java.io.Serializable;

@Getter
@Setter
public class MessageDTO implements Serializable {
    private EnumBankMessages bankMessage;
    private Object object;
}
