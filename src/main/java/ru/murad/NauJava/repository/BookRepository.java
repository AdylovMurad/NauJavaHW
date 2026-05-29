package ru.murad.NauJava.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.dao.BookRepositoryCustom;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "books", path = "books")
public interface BookRepository extends org.springframework.data.repository.CrudRepository<Book, Long>, BookRepositoryCustom {
    List<Book> findByTitleContainingAndPublicationYearBetween(String titlePart, Integer startYear, Integer endYear);

    @Query("SELECT b FROM Book b WHERE b.author.fullName = :name")
    List<Book> findByAuthorFullName(@Param("name") String name);

        @Query("""
                        SELECT b FROM Book b
                        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
                            AND (:author IS NULL OR LOWER(b.author.fullName) LIKE LOWER(CONCAT('%', :author, '%')))
                            AND (:genre IS NULL OR LOWER(b.genre.name) LIKE LOWER(CONCAT('%', :genre, '%')))
                        """)
        List<Book> searchBooks(@Param("title") String title,
                                                     @Param("author") String author,
                                                     @Param("genre") String genre);
}