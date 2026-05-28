package ru.murad.NauJava;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.murad.NauJava.entity.*;
import ru.murad.NauJava.repository.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BookGeneratorTest {
    protected List<Book> books;
    protected List<Author> authors;

    @Autowired
    protected BookRepository bookRepository;
    @Autowired
    protected AuthorRepository authorRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        cleanUp();
        createAdminUser();

        authors = new ArrayList<>();
        Author author = new Author();
        author.setFullName("Лев Толстой");
        author.setBiography("Великий русский писатель");
        author.setBirthDate(LocalDate.of(1828, 9, 9));
        author = authorRepository.save(author);
        authors.add(author);

        books = new ArrayList<>();
        Book book = new Book();
        book.setTitle("Война и мир");
        book.setAuthor(author);
        book.setPublicationYear(1869);
        book.setLanguage("Русский");
        book.setIsbn("ISBN-" + UUID.randomUUID().toString().substring(0, 8));
        book.setDescription("Эпический роман");

        book = bookRepository.save(book);
        books.add(book);
    }

    private void createAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setFirstName("Admin");
            admin.setLastName("System");
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }
    }

    @AfterEach
    public void cleanUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        userRepository.deleteAll();
    }
}