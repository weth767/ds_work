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
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@Entity
public class Extract implements Serializable {
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

    @ManyToOne(cascade = CascadeType.ALL)
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

    public String toViewExtract() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return format.format(date) + " - " + timeFormat.format(time) + ", R$" + value
                + (operation.equals(EnumOperationType.TRANSFER) ? ", Transferência": ", Recebimento");
    }
}
