package fezas.decodez.model;

import fezas.decodez.entity.Setting;
import fezas.decodez.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Класс формирования структур и объектов типа {@link Setting}
 * @autor Stepantsov P.V.
 */

public class SettingModel {
    private static final Logger logger = LogManager.getLogger();
    public SettingModel() {
    }

    public static Setting getSetting() {
        Transaction transaction = null;
        Setting result = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            result = session.byId(Setting.class).load(0L);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return result;
    }

    public static void saveOrUpdateCategory(Setting setting) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(setting);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
