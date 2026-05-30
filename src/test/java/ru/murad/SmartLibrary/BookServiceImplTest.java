package ru.murad.SmartLibrary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.murad.SmartLibrary.entity.Author;
import ru.murad.SmartLibrary.entity.Book;
import ru.murad.SmartLibrary.repository.AuthorRepository;
import ru.murad.SmartLibrary.repository.BookRepository;
import ru.murad.SmartLibrary.service.BookServiceImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    public void createBook_SavesAuthorAndBook() {
        Author savedAuthor = new Author();
        savedAuthor.setId(10L);
        savedAuthor.setFullName("Leo Tolstoy");
        Mockito.when(authorRepository.save(Mockito.any(Author.class))).thenReturn(savedAuthor);

        bookService.createBook(null, "War and Peace", "Leo Tolstoy");

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookRepository).save(bookCaptor.capture());
        Book savedBook = bookCaptor.getValue();

        Assertions.assertEquals("War and Peace", savedBook.getTitle());
        Assertions.assertEquals(5, savedBook.getAvailableCount());
        Assertions.assertNotNull(savedBook.getAuthor());
        Assertions.assertEquals("Leo Tolstoy", savedBook.getAuthor().getFullName());
        Mockito.verify(authorRepository).save(Mockito.any(Author.class));
    }

    @Test
    public void getAllBooks_ReturnsList() {
        Book book1 = new Book();
        book1.setId(1L);
        Book book2 = new Book();
        book2.setId(2L);
        Mockito.when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> result = bookService.getAllBooks();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
    }

    @Test
    public void updateBook_UpdatesAuthorAndBook() {
        Author author = new Author();
        author.setFullName("Old Name");
        Book book = new Book();
        book.setId(5L);
        book.setAuthor(author);
        Mockito.when(bookRepository.findById(5L)).thenReturn(Optional.of(book));

        bookService.updateBook(5L, "New Title", "New Name");

        Assertions.assertEquals("New Title", book.getTitle());
        Assertions.assertEquals("New Name", book.getAuthor().getFullName());
        Mockito.verify(authorRepository).save(author);
        Mockito.verify(bookRepository).save(book);
    }

    @Test
    public void deleteBook_DelegatesToRepository() {
        bookService.deleteBook(7L);
        Mockito.verify(bookRepository).deleteById(7L);
    }

    @Test
    public void findById_ReturnsOptional() {
        Book book = new Book();
        book.setId(3L);
        Mockito.when(bookRepository.findById(3L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.findById(3L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(3L, result.get().getId());
    }
}
