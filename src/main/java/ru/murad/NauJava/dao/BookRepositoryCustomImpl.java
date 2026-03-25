package ru.murad.NauJava.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.entity.Book;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findBooksByTitleAndYearRange(String titlePart, Integer startYear, Integer endYear) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        List<Predicate> predicates = new ArrayList<>();

        if (titlePart != null && !titlePart.isEmpty()) {
            predicates.add(cb.like(book.get("title"), "%" + titlePart + "%"));
        }

        if (startYear != null && endYear != null) {
            predicates.add(cb.between(book.get("publicationYear"), startYear, endYear));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Book> findBooksByAuthorName(String authorName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        Join<Book, Author> authorJoin = book.join("author", JoinType.INNER);

        Predicate authorPredicate = cb.equal(authorJoin.get("fullName"), authorName);

        cq.select(book).where(authorPredicate);
        return entityManager.createQuery(cq).getResultList();
    }
}