package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.Constants;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private BigDecimal balance;

    @OneToOne(cascade = CascadeType.ALL)
    private Person owner;

    public Account() {
        this.balance = new BigDecimal(Constants.START_VALUE);
    }
}
