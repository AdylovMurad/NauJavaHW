package ru.murad.SmartLibrary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.SmartLibrary.controller.dto.AuthorRequest;
import ru.murad.SmartLibrary.entity.Author;
import ru.murad.SmartLibrary.exception.ResourceNotFoundException;
import ru.murad.SmartLibrary.repository.AuthorRepository;
import ru.murad.SmartLibrary.service.AuthorService;

import java.util.ArrayList;
import java.util.List;

/**
 * REST-контроллер для управления авторами книг через API.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorRestController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorRestController.class);

    private final AuthorRepository authorRepository;
    private final AuthorService authorService;

    /**
     * Конструктор контроллера AuthorRestController.
     *
     * @param authorRepository репозиторий авторов
     * @param authorService    сервис управления авторами
     */
    public AuthorRestController(AuthorRepository authorRepository, AuthorService authorService) {
        this.authorRepository = authorRepository;
        this.authorService = authorService;
    }

    /**
     * Возвращает список всех зарегистрированных авторов.
     *
     * @return ResponseEntity со списком авторов
     */
    @GetMapping
    public ResponseEntity<List<Author>> getAll() {
        List<Author> authors = new ArrayList<>();
        authorRepository.findAll().forEach(authors::add);
        return ResponseEntity.ok(authors);
    }

    /**
     * Возвращает автора по его идентификатору.
     *
     * @param id идентификатор автора
     * @return ResponseEntity с найденным автором
     */
    @GetMapping("/{id}")
    public ResponseEntity<Author> getById(@PathVariable Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Автор не найден"));
        return ResponseEntity.ok(author);
    }

    /**
     * Создает новую запись автора.
     *
     * @param request DTO с параметрами нового автора
     * @return ResponseEntity с созданным автором
     */
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

    /**
     * Обновляет существующую запись автора.
     *
     * @param id      идентификатор автора
     * @param request DTO с обновленными данными автора
     * @return ResponseEntity с обновленным автором
     */
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

    /**
     * Удаляет автора и связанные с ним книги.
     *
     * @param id идентификатор автора
     * @return ResponseEntity без тела (no content)
     */
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
