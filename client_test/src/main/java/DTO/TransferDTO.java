package DTO;

import lombok.Getter;
import lombok.Setter;
import model.Account;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferDTO implements Serializable {
    private Account source;
    private Account target;
    private BigDecimal value;
}
