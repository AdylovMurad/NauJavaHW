package ru.murad.NauJava.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.murad.NauJava.repository.BookRepository;

@Controller
public class BookViewController {

    private final BookRepository bookRepository;

    public BookViewController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/ui/books")
    public String showAllBooks(Model model) {
        model.addAttribute("books", bookRepository.findAll());
        return "books";
    }
}