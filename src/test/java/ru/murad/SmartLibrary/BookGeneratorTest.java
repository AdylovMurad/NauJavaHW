package ru.murad.SmartLibrary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.murad.SmartLibrary.entity.*;
import ru.murad.SmartLibrary.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public abstract class BookGeneratorTest extends BaseIntegrationTest {
    
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
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        userRepository.deleteAll();

        createAdminUser();

        authors = new ArrayList<>();
        Author author = new Author();
        author.setFullName("Лев Толстой");
        author.setBiography("Великий русский писатель");
        author.setBirthDate(LocalDate.of(1828, 9, 9));
        author = authorRepository.save(author);
        authors.add(author);

        books = new ArrayList<>();
        
        Book book1 = new Book();
        book1.setTitle("Война и мир");
        book1.setAuthor(author);
        book1.setPublicationYear(1869);
        book1.setLanguage("Русский");
        book1.setIsbn("ISBN-" + UUID.randomUUID().toString().substring(0, 8));
        book1.setDescription("Эпический роман о войне 1812 года");
        book1.setAvailableCount(5);
        book1 = bookRepository.save(book1);
        books.add(book1);
        
        Book book2 = new Book();
        book2.setTitle("Анна Каренина");
        book2.setAuthor(author);
        book2.setPublicationYear(1877);
        book2.setLanguage("Русский");
        book2.setIsbn("ISBN-" + UUID.randomUUID().toString().substring(0, 8));
        book2.setDescription("Роман о любви");
        book2.setAvailableCount(3);
        book2 = bookRepository.save(book2);
        books.add(book2);
        
        Book book3 = new Book();
        book3.setTitle("Детство");
        book3.setAuthor(author);
        book3.setPublicationYear(1852);
        book3.setLanguage("Русский");
        book3.setIsbn("ISBN-" + UUID.randomUUID().toString().substring(0, 8));
        book3.setDescription("Автобиографическая повесть");
        book3.setAvailableCount(2);
        book3 = bookRepository.save(book3);
        books.add(book3);
        
        System.out.println("=== Тестовые данные созданы ===");
        System.out.println("Всего книг: " + books.size());
        for (Book b : books) {
            System.out.println("  Книга: " + b.getTitle() + " (ID: " + b.getId() + ", год: " + b.getPublicationYear() + ")");
        }
    }

    private void createAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setFirstName("Admin");
            admin.setLastName("System");
            admin.setRole(UserRole.ROLE_ADMIN);
            admin.setEmail("admin@test.com");
            userRepository.save(admin);
            System.out.println("Администратор создан: admin/adminpass");
        }
    }

    @AfterEach
    public void cleanUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        userRepository.deleteAll();
        System.out.println("=== Данные очищены после теста ===");
    }
}