package controller;

import model.Account;
import model.Extract;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class ExtractController extends Controller {
    public ExtractController(SessionFactory factory) {
        super(factory);
    }

    public List<Extract> findAllByAccountId(Long id) {
        this.session = this.factory.openSession();
        Transaction transaction = this.session.beginTransaction();
        List<Extract> extracts = session.createSQLQuery("select * from Extract where account_id in (select id from Account where Account.id = :id)")
                .addEntity(Extract.class).setParameter("id", id).list();
        session.flush();
        transaction.commit();
        session.close();
        return extracts;
    }
}
