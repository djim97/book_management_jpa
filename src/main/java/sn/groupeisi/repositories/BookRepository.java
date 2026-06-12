package sn.groupeisi.repositories;

import jakarta.persistence.EntityManager;

public class BookRepository {
    EntityManager em = JpaUtil.getEntityManager();
}
