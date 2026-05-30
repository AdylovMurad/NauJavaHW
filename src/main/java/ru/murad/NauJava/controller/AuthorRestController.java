package ru.murad.NauJava.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.NauJava.controller.dto.AuthorRequest;
import ru.murad.NauJava.entity.Author;
import ru.murad.NauJava.exception.ResourceNotFoundException;
import ru.murad.NauJava.repository.AuthorRepository;
import ru.murad.NauJava.service.AuthorService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorRestController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorRestController.class);

    private final AuthorRepository authorRepository;
    private final AuthorService authorService;

    public AuthorRestController(AuthorRepository authorRepository, AuthorService authorService) {
        this.authorRepository = authorRepository;
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAll() {
        List<Author> authors = new ArrayList<>();
        authorRepository.findAll().forEach(authors::add);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getById(@PathVariable Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автор не найден"));
        return ResponseEntity.ok(author);
    }

    @PostMapping
    public ResponseEntity<Author> create(@RequestBody AuthorRequest request) {
        Author author = new Author();
        author.setFullName(request.getFullName());
        author.setBiography(request.getBiography());
        author.setBirthDate(request.getBirthDate());
        author.setDateOfDeath(request.getDateOfDeath());
        Author saved = authorRepository.save(author);
        logger.info("REST created author id={} name='{}'", saved.getId(), saved.getFullName());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> update(@PathVariable Long id, @RequestBody AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автор не найден"));

        author.setFullName(request.getFullName());
        author.setBiography(request.getBiography());
        author.setBirthDate(request.getBirthDate());
        author.setDateOfDeath(request.getDateOfDeath());
        Author saved = authorRepository.save(author);
        logger.info("REST updated author id={} name='{}'", saved.getId(), saved.getFullName());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Автор не найден");
        }
        authorService.deleteAuthorWithBooks(id);
        logger.info("REST deleted author id={}", id);
        return ResponseEntity.noContent().build();
    }
}
