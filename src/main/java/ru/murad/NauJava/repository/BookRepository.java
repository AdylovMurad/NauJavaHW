package ru.murad.NauJava.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.murad.NauJava.entity.Book;

import java.util.List;
import java.util.Optional;

@Component
public class BookRepository implements CrudRepository<Book, Long> {

    private final List<Book> bookContainer;

    @Autowired
    public BookRepository(List<Book> bookContainer) {
        this.bookContainer = bookContainer;
    }

    @Override
    public void create(Book book) {
        bookContainer.add(book);
    }

    @Override
    public Book read(Long id) {
        return bookContainer.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Book updatedBook) {
        for (int i = 0; i < bookContainer.size(); i++) {
            if (bookContainer.get(i).getId().equals(updatedBook.getId())) {
                bookContainer.set(i, updatedBook);
                return;
            }
        }
    }

    @Override
    public void delete(Long id) {
        bookContainer.removeIf(book -> book.getId().equals(id));
    }
    public List<Book> findAll() {
        return bookContainer;
    }
}
