package ru.murad.SmartLibrary.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.dao.BookRepositoryCustom;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "books", path = "books")
public interface BookRepository extends org.springframework.data.repository.CrudRepository<Book, Long>, BookRepositoryCustom {
    List<Book> findByTitleContainingAndPublicationYearBetween(String titlePart, Integer startYear, Integer endYear);

    @Query("SELECT b FROM Book b WHERE b.author.fullName = :name")
    List<Book> findByAuthorFullName(@Param("name") String name);

    @Query("""
            SELECT b FROM Book b
            LEFT JOIN b.author a
            LEFT JOIN b.genre g
            WHERE (CAST(:title AS string) IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%')))
              AND (CAST(:author AS string) IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', CAST(:author AS string), '%')))
              AND (CAST(:genre AS string) IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', CAST(:genre AS string), '%')))
            """)
    List<Book> searchBooks(@Param("title") String title,
                           @Param("author") String author,
                           @Param("genre") String genre);
}
