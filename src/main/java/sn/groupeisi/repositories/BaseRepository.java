package sn.groupeisi.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public abstract class BaseRepository {

    protected EntityManager em = JpaUtil.getEntityManager();

    protected void executeInTransaction(Runnable action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.run();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erreur transaction : " + e.getMessage(), e);
        }
    }
}