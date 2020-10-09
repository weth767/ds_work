package model;

import lombok.Getter;
import lombok.Setter;
import model.enumeration.EnumOperationType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@Entity
public class Extract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date date;

    @Column
    private Time time;

    @Enumerated(EnumType.ORDINAL)
    @Column
    private EnumOperationType operation;

    @Column
    private BigDecimal value;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Account account;

    @Override
    public String toString() {
        return "Extract{" +
                "date=" + date +
                ", time=" + time +
                ", operation=" + operation +
                ", value=" + value +
                '}';
    }
}
