package sn.groupeisi.repositories;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import sn.groupeisi.entities.Category;
import java.util.List;

public class CategoryRepository extends BaseRepository {

    public Category create(Category cat)
    {

        Category category = new Category();
        category.setName(cat.getName());
        category.setState(Boolean.TRUE);
        category.setUserCreated("admin");
        category.setUserUpdated("admin");


        executeInTransaction(() -> em.persist(category));
        return category;
    }
    public Category findCategoryById(int id) {
        Category c = em.find(Category.class, id);

        if (c == null) throw new EntityNotFoundException("Category introuvable : " + id);
        return c;
    }

    public void updateCategory(int id, Category newCategory) {
        executeInTransaction(() -> {
            Category c = em.find(Category.class, id);
            if (c != null) {
                c.setName(newCategory.getName());
                c.setState(newCategory.isState());
                c.setUserUpdated("admin");
            }
        });
    }

    public void deleteCategory(int id) {
        executeInTransaction(() -> {
            Category c = em.find(Category.class, id);
            if (c != null) em.remove(c);
        });
    }
    public List<Category> getAllCategories() {
        return em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class).getResultList();
    }

    public List<Category> searchCategoriesByName(String keyword) {
        return em.createQuery(
                        "SELECT c FROM Category c WHERE LOWER(c.name) LIKE :kw ORDER BY c.name",
                        Category.class)
                .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                .getResultList();
    }

    public List<Category> listActiveCategories() {
        return em.createQuery(
                        "SELECT c FROM Category c WHERE c.state = true ORDER BY c.name",
                        Category.class)
                .getResultList();
    }

    public long countAllCategories() {
        return em.createQuery("SELECT COUNT(c) FROM Category c", Long.class)
                .getSingleResult();
    }

}

