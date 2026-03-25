package ru.murad.NauJava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.repository.AuthorRepository;
import ru.murad.NauJava.repository.BookRepository;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testSearchMethods() {
        Author author = new Author();
        author.setFullName("Тестовый Автор " + UUID.randomUUID());
        authorRepository.save(author);

        Book book = new Book();
        book.setTitle("Уникальное Название");
        book.setPublicationYear(2020);
        book.setAuthor(author);
        bookRepository.save(book);

        List<Book> foundByQuery = bookRepository.findByTitleContainingAndPublicationYearBetween(
                "Уникальное", 2019, 2026);
        Assertions.assertFalse(foundByQuery.isEmpty());

        List<Book> foundByCriteriaAuthor = bookRepository.findBooksByAuthorName(author.getFullName());
        Assertions.assertFalse(foundByCriteriaAuthor.isEmpty());
        Assertions.assertEquals(author.getFullName(), foundByCriteriaAuthor.getFirst().getAuthor().getFullName());

        List<Book> foundByCriteriaRange = bookRepository.findBooksByTitleAndYearRange("Уникальное", 2019, 2026);
        Assertions.assertFalse(foundByCriteriaRange.isEmpty());
    }

    @Test
    void testCriteriaPartialSearch() {

        Book partialBook = new Book();
        partialBook.setTitle("Частичный Поиск Тест");
        partialBook.setPublicationYear(1995);
        bookRepository.save(partialBook);

        List<Book> results = bookRepository.findBooksByTitleAndYearRange("Частичный", null, null);

        Assertions.assertFalse(results.isEmpty());
        Assertions.assertTrue(results.stream().anyMatch(b -> b.getTitle().contains("Частичный")));
    }

    @Test
    void testFindMultipleBooksByAuthor() {
        Author author = new Author();
        author.setFullName("Автор с несколькими книгами " + UUID.randomUUID());
        authorRepository.save(author);

        Book b1 = new Book(); b1.setTitle("Книга 1"); b1.setAuthor(author);
        Book b2 = new Book(); b2.setTitle("Книга 2"); b2.setAuthor(author);
        bookRepository.saveAll(List.of(b1, b2));

        List<Book> books = bookRepository.findBooksByAuthorName(author.getFullName());

        Assertions.assertEquals(2, books.size(), "Должно быть найдено ровно 2 книги автора");
    }
}