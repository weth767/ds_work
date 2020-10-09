package controller;

import model.Account;
import model.User;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AccountController extends Controller {

    public AccountController(SessionFactory factory) {
        super(factory);
    }

    public BigDecimal getBalance(Long id) {
        this.session = this.factory.openSession();
        Transaction transaction = this.session.beginTransaction();
        Query query = session.createQuery("from Account where id = :id");
        query.setParameter("id", id);
        List list = query.list();
        session.flush();
        transaction.commit();
        session.close();
        Account account = (Account) list.stream().findFirst().orElse(null);
        return Objects.nonNull(account) ? account.getBalance() : new BigDecimal("0");
    }

    public BigDecimal getAmount() {
        BigDecimal sum = new BigDecimal("0");
        List<Object> accountList = super.findAll("Account");
        for (Object obj : accountList) {
            Account account = (Account) obj;
            sum = sum.add(account.getBalance());
        }
        return sum;
    }
}
