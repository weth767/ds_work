package controller;

import model.User;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class UserController extends Controller {

    public UserController(SessionFactory factory) {
        super(factory);
    }

    public User findByUserPassWord(String user, String password) {
        this.session = this.factory.openSession();
        Transaction transaction = this.session.beginTransaction();
        Query query = session.createQuery("from User where username = :user and password = :password");
        query.setParameter("user", user);
        query.setParameter("password", password);
        List list = query.list();
        session.flush();
        transaction.commit();
        session.close();
        return (User) list.stream().findFirst().orElse(null);
    }
}
