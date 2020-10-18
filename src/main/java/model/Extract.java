package model;

import lombok.Getter;
import lombok.Setter;
import model.enumeration.EnumOperationType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class Extract implements Serializable {
    private Date date;
    private Time time;
    private EnumOperationType operation;
    private BigDecimal value;
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
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        return format.format(date) + " - " + timeFormat.format(time) + ", R$ " + new DecimalFormat("#,##0.00", decimalFormatSymbols).format(value)
                + (operation.equals(EnumOperationType.TRANSFER) ? ", Transferência": ", Recebimento");
    }
}
