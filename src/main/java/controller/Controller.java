package controller;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class Controller {
    private SessionFactory factory;
    private Session session;

    public Controller(SessionFactory factory) {
        this.factory = factory;
    }

    public Object save(Object object) {
        this.session = this.factory.openSession();
        Transaction transaction = this.session.beginTransaction();
        Serializable savedObject = this.session.save(object);
        session.flush();
        transaction.commit();
        session.close();
        return savedObject;
    }

    public List<Object> findAll(String table) {
        this.session = this.factory.openSession();
        Transaction transaction = this.session.beginTransaction();
        List<Object> findedObjects = session.createQuery("from " + table).list();
        session.flush();
        transaction.commit();
        session.close();
        return findedObjects;
    }

    public Object findById(Long id, String table) {
        this.session = this.factory.openSession();
        Transaction transaction = this.session.beginTransaction();
        Query query = session.createQuery("from " + table + " where id = :id ");
        query.setParameter("id", id);
        List list = query.list();
        session.flush();
        transaction.commit();
        session.close();
        return list.stream().findFirst().orElse(null);
    }
}
