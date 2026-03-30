package ru.murad.NauJava.dao;

import ru.murad.NauJava.entity.Book;
import java.util.List;

public interface BookRepositoryCustom {
    List<Book> findBooksByTitleAndYearRange(String titlePart, Integer startYear, Integer endYear);

    List<Book> findBooksByAuthorName(String authorName);
}
