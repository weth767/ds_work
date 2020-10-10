package model;

import lombok.Getter;
import lombok.Setter;
import utils.Constants;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private BigDecimal balance;

    @OneToOne(cascade = CascadeType.ALL)
    private Person owner;

    @OneToMany(mappedBy = "account")
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
