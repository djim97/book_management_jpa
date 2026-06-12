package sn.groupeisi.repositories;

import jakarta.persistence.EntityManager;

public class CategoryRepository {
    EntityManager em = JpaUtil.getEntityManager();
}
