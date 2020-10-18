package model;

import lombok.Getter;
import lombok.Setter;
import utils.Constants;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Account implements Serializable {
    private Long id;
    private BigDecimal balance;
    private User owner;
    private List<Extract> extracts = new ArrayList<>();


    public Account() {
        this.balance = new BigDecimal(Constants.START_VALUE);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }
}
