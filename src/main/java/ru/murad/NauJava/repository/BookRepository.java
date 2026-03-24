package ru.murad.NauJava.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.dao.BookRepositoryCustom;
import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long>, BookRepositoryCustom {
    List<Book> findByTitleContainingAndPublicationYearBetween(String titlePart, Integer startYear, Integer endYear);

    @Query("SELECT b FROM Book b WHERE b.author.fullName = :name")
    List<Book> findByAuthorFullName(@Param("name") String name);
}