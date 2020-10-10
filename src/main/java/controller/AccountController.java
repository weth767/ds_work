package controller;

import model.Account;
import model.Extract;
import model.User;
import model.enumeration.EnumOperationType;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;
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

    public Account getAccountByCpf(String cpf) {
        this.session = this.factory.openSession();
        Transaction transaction = this.session.beginTransaction();
        List<Account> accounts = session.createSQLQuery("select * from Account where owner_id in (select id from Person where Person.cpf = :cpf)")
                .addEntity(Account.class).setParameter("cpf", cpf).list();
        session.flush();
        transaction.commit();
        session.close();
        return accounts.stream().findFirst().orElse(null);
    }

    public boolean transferValue(Account source, String cpf, BigDecimal value) {
        BigDecimal currentBalance = getBalance(source.getId());
        Account target = getAccountByCpf(cpf);
        if (currentBalance.compareTo(value) >= 0 && Objects.nonNull(target)) {
            this.session = this.factory.openSession();
            Transaction transaction = this.session.beginTransaction();
            BigDecimal newBalance = currentBalance.subtract(value);
            /*atualizou os dados de quem transferiu*/
            source.setBalance(newBalance);
            session.update(source);
            /*agora atualiza os dados de quem recebeu*/
            newBalance = target.getBalance().add(value);
            target.setBalance(newBalance);
            session.update(target);
            /*atualiza os extratos*/

            Extract extract = new Extract();
            extract.setAccount(source);
            extract.setValue(value);
            extract.setDate(new Date());
            extract.setTime(Time.valueOf(LocalTime.now()));
            extract.setOperation(EnumOperationType.TRANSFER);
            session.save(extract);

            Extract extractTarget = new Extract();
            extractTarget.setAccount(target);
            extractTarget.setValue(value);
            extractTarget.setDate(new Date());
            extractTarget.setTime(Time.valueOf(LocalTime.now()));
            extractTarget.setOperation(EnumOperationType.RECEIVE);
            session.save(extractTarget);

            session.flush();
            transaction.commit();
            session.close();
            return true;
        }
        return false;
    }
}
