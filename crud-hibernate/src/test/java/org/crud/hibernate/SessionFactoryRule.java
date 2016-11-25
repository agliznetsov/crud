package org.crud.hibernate;

import org.crud.hibernate.model.Client;
import org.crud.hibernate.model.Document;
import org.crud.hibernate.model.DocumentItem;
import org.crud.hibernate.model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.HashMap;
import java.util.Map;

public class SessionFactoryRule implements MethodRule {
    private SessionFactory sessionFactory;
    private Transaction transaction;
    private Session session;

    @Override
    public Statement apply(final Statement statement, FrameworkMethod method, Object test) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                sessionFactory = createSessionFactory();
                createSession();
                beginTransaction();
                try {
                    statement.evaluate();
                } finally {
                    shutdown();
                }
            }
        };
    }

    private void shutdown() {
        try {
            try {
//                try {
//                    transaction.rollback();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
                session.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            sessionFactory.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private SessionFactory createSessionFactory() {
        Map settings = new HashMap();
        settings.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        settings.put("hibernate.connection.driver_class", "org.h2.Driver");
        settings.put("hibernate.connection.url", "jdbc:h2:mem:");
        settings.put("hibernate.hbm2ddl.auto", "create");

        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        Metadata metadata = new MetadataSources(standardRegistry)
                .addAnnotatedClass(Document.class)
                .addAnnotatedClass(DocumentItem.class)
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Client.class)
                .getMetadataBuilder()
                .build();

        return metadata.getSessionFactoryBuilder().build();
    }

    public Session createSession() {
        session = sessionFactory.openSession();
        return session;
    }

    public void commit() {
        transaction.commit();
    }

    public void beginTransaction() {
        transaction = session.beginTransaction();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session getSession() {
        return session;
    }

    public void flush() {
        session.flush();
        session.clear();
    }
}