package sn.groupeisi.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import sn.groupeisi.entities.Book;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class BookRepository extends BaseRepository {
    EntityManager em = JpaUtil.getEntityManager();

    public void createBook(Book book, String username) {
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            book.setIsbn(generateIsbn());
        }
        book.setUserCreated(username);
        book.setUserUpdated(username);
        executeInTransaction(() -> em.persist(book));
    }

    public List<Book> listAllBooks() {
        return em.createQuery(
                        "SELECT b FROM Book b ORDER BY b.title",
                        Book.class)
                .getResultList();
    }

    public Book findBookById(int id) {
        Book b = em.find(Book.class, id);
        if (b == null) throw new EntityNotFoundException("Book introuvable : " + id);
        return b;
    }

    public Book findBookByIsbn(String isbn) {
        return em.createQuery(
                        "SELECT b FROM Book b WHERE b.isbn = :isbn",
                        Book.class)
                .setParameter("isbn", isbn)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Book introuvable avec ISBN : " + isbn));
    }

    public void updateBook(int id, Book newBook, String username) {
        executeInTransaction(() -> {
            Book b = em.find(Book.class, id);
            if (b != null) {
                b.setTitle(newBook.getTitle());
                b.setAuthor(newBook.getAuthor());
                b.setPublicationYear(newBook.getPublicationYear());
                b.setCountPages(newBook.getCountPages());
                b.setCategory(newBook.getCategory());
                b.setUserUpdated(username);
            }
        });
    }

    public void deleteBook(int id) {
        executeInTransaction(() -> {
            Book b = em.find(Book.class, id);
            if (b != null) em.remove(b);
        });
    }
    public List<Book> listBooksByCategory(String categoryName) {
        return em.createQuery(
                        "SELECT b FROM Book b WHERE LOWER(b.category.name) = :name ORDER BY b.title",
                        Book.class)
                .setParameter("name", categoryName.toLowerCase())
                .getResultList();
    }

    public List<Book> searchBooksByTitle(String keyword) {
        return em.createQuery(
                        "SELECT b FROM Book b WHERE LOWER(b.title) LIKE :kw ORDER BY b.title",
                        Book.class)
                .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                .getResultList();
    }

    public List<Book> searchBooksByAuthor(String keyword) {
        return em.createQuery(
                        "SELECT b FROM Book b WHERE LOWER(b.author) LIKE :kw ORDER BY b.author",
                        Book.class)
                .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                .getResultList();
    }

    public List<Book> searchBooksAfterYear(int year) {
        return em.createQuery(
                        "SELECT b FROM Book b WHERE b.publicationYear > :year ORDER BY b.publicationYear",
                        Book.class)
                .setParameter("year", year)
                .getResultList();
    }
    public long countAllBooks() {
        return em.createQuery(
                        "SELECT COUNT(b) FROM Book b", Long.class)
                .getSingleResult();
    }

    public Map<String, Long> countBooksByCategory() {
        List<Object[]> rows = em.createQuery(
                        "SELECT b.category.name, COUNT(b) FROM Book b GROUP BY b.category.name",
                        Object[].class)
                .getResultList();

        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    private String generateIsbn() {
        String[] prefixes = {"978", "979"};
        Random random = new Random();
        String prefix = prefixes[random.nextInt(2)];
        String group = String.valueOf(random.nextInt(2));
        String publisher = String.format("%04d", random.nextInt(10000));
        String title = String.format("%04d", random.nextInt(10000));
        String base = prefix + group + publisher + title;
        int checkDigit = computeIsbn13CheckDigit(base);
        return String.format("%s-%s-%s-%s-%d",
                prefix, group, publisher, title, checkDigit);
    }

    private int computeIsbn13CheckDigit(String base12) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(base12.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int remainder = sum % 10;
        return remainder == 0 ? 0 : 10 - remainder;
    }
}

