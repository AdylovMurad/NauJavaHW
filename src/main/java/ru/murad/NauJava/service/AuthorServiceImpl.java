package ru.murad.NauJava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.entity.Book;
import ru.murad.NauJava.repository.AuthorRepository;
import ru.murad.NauJava.repository.BookRepository;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository,
                             BookRepository bookRepository,
                             PlatformTransactionManager transactionManager) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void deleteAuthorWithBooks(Long authorId) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new RuntimeException("Автор не найден с ID: " + authorId));

            List<Book> authorBooks = bookRepository.findByAuthorFullName(author.getFullName());

            bookRepository.deleteAll(authorBooks);
            authorRepository.deleteById(authorId);

            transactionManager.commit(status);
            System.out.println("Транзакция завершена успешно.");

        } catch (RuntimeException ex) {
            transactionManager.rollback(status);
            System.err.println("Транзакция откачена: " + ex.getMessage());
            throw ex;
        }
    }
}