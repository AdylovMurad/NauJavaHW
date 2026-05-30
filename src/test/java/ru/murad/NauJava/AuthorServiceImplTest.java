package ru.murad.NauJava;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.exception.ResourceNotFoundException;
import ru.murad.NauJava.repository.AuthorRepository;
import ru.murad.NauJava.repository.BookRepository;
import ru.murad.NauJava.service.AuthorServiceImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    public void deleteAuthorWithBooks_DeletesBooksAndAuthor() {
        Author author = new Author();
        author.setId(1L);
        author.setFullName("Author A");
        Mockito.when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        Book book = new Book();
        book.setId(10L);
        Mockito.when(bookRepository.findByAuthorFullName("Author A")).thenReturn(List.of(book));

        authorService.deleteAuthorWithBooks(1L);

        Mockito.verify(bookRepository).deleteAll(List.of(book));
        Mockito.verify(authorRepository).delete(author);
    }

    @Test
    public void deleteAuthorWithBooks_NotFound_Throws() {
        Mockito.when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> authorService.deleteAuthorWithBooks(99L));
    }
}
