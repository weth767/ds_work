package connection;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.Objects;
import java.util.Properties;

public class Connection {
    private static SessionFactory sessionFactory = null;

    private static SessionFactory configureSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.configure();

        Properties properties = configuration.getProperties();
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(properties).buildServiceRegistry();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static SessionFactory getConnection() {
        if (Objects.isNull(sessionFactory)) { sessionFactory = configureSessionFactory(); }
        return sessionFactory;
    }
}
