/*
 * Copyright (c) 2022-2023. Stepantsov P.V.
 */

package fezas.decodez.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger();
    private static SessionFactory sessionFactory = buildSessionFactory();
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            System.err.println("Initial SessionFactory created failed. " + ex);
            logger.error("ERROR: ", "Initial SessionFactory created failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {return sessionFactory;}

    public static void shutdown(){
        getSessionFactory().close();
    }
}
