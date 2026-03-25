package ru.murad.NauJava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.repository.AuthorRepository;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.service.AuthorService;

import java.util.UUID;

@SpringBootTest
class AuthorTransactionTest {

    @Autowired
    private AuthorService authorService;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    @Test
    void testDeleteAuthorWithBooksSuccess() {
        Author author = new Author();
        author.setFullName("Автор для удаления " + UUID.randomUUID());
        authorRepository.save(author);

        Book book = new Book();
        book.setTitle("Книга для удаления");
        book.setAuthor(author);
        bookRepository.save(book);

        authorService.deleteAuthorWithBooks(author.getId());

        Assertions.assertTrue(authorRepository.findById(author.getId()).isEmpty());
        Assertions.assertTrue(bookRepository.findById(book.getId()).isEmpty());
    }

    @Test
    void testDeleteAuthorWithBooksRollback() {
        Long fakeId = 999999L;

        Assertions.assertThrows(Exception.class, () -> {
            authorService.deleteAuthorWithBooks(fakeId);
        });
    }

    @Test
    void testDeleteAuthorWithoutBooksSuccess() {
        Author lonelyAuthor = new Author();
        lonelyAuthor.setFullName("Одинокий Автор " + UUID.randomUUID());
        authorRepository.save(lonelyAuthor);
        Long id = lonelyAuthor.getId();

        Assertions.assertDoesNotThrow(() -> {
            authorService.deleteAuthorWithBooks(id);
        });

        Assertions.assertTrue(authorRepository.findById(id).isEmpty());
    }
}