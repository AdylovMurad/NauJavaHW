package ru.murad.SmartLibrary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.murad.SmartLibrary.entity.Author;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.repository.AuthorRepository;
import ru.murad.SmartLibrary.repository.BookRepository;
import ru.murad.SmartLibrary.service.AuthorService;

import java.util.UUID;

@SpringBootTest
@Transactional
class AuthorTransactionTest extends BaseIntegrationTest {

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
        author = authorRepository.save(author);

        Book book = new Book();
        book.setTitle("Книга для удаления");
        book.setAuthor(author);
        book.setAvailableCount(1);
        book = bookRepository.save(book);

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
        lonelyAuthor = authorRepository.save(lonelyAuthor);
        Long id = lonelyAuthor.getId();

        Assertions.assertDoesNotThrow(() -> {
            authorService.deleteAuthorWithBooks(id);
        });

        Assertions.assertTrue(authorRepository.findById(id).isEmpty());
    }
}