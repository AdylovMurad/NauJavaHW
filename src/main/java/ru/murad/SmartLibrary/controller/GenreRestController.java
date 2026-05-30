package ru.murad.SmartLibrary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.SmartLibrary.controller.dto.GenreRequest;
import ru.murad.SmartLibrary.entity.Genre;
import ru.murad.SmartLibrary.exception.ResourceNotFoundException;
import ru.murad.SmartLibrary.repository.GenreRepository;

import java.util.List;

/**
 * REST-контроллер для управления жанрами книг через API.
 */
@RestController
@RequestMapping("/api/genres")
public class GenreRestController {

    private static final Logger logger = LoggerFactory.getLogger(GenreRestController.class);

    private final GenreRepository genreRepository;

    /**
     * Конструктор контроллера GenreRestController.
     *
     * @param genreRepository репозиторий жанров
     */
    public GenreRestController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Возвращает список всех зарегистрированных жанров.
     *
     * @return ResponseEntity со списком жанров
     */
    @GetMapping
    public ResponseEntity<List<Genre>> getAll() {
        return ResponseEntity.ok(genreRepository.findAll());
    }

    /**
     * Возвращает жанр по его идентификатору.
     *
     * @param id идентификатор жанра
     * @return ResponseEntity с найденным жанром
     */
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getById(@PathVariable Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Жанр не найден"));
        return ResponseEntity.ok(genre);
    }

    /**
     * Создает новый литературный жанр.
     *
     * @param request DTO с параметрами нового жанра
     * @return ResponseEntity с созданным жанром
     */
    @PostMapping
    public ResponseEntity<Genre> create(@RequestBody GenreRequest request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        genre.setPopularityIndex(request.getPopularityIndex());
        Genre saved = genreRepository.save(genre);
        logger.info("REST created genre id={} name='{}'", saved.getId(), saved.getName());
        return ResponseEntity.ok(saved);
    }

    /**
     * Обновляет параметры существующего жанра.
     *
     * @param id      идентификатор жанра
     * @param request DTO с обновленными данными
     * @return ResponseEntity с обновленным жанром
     */
    @PutMapping("/{id}")
    public ResponseEntity<Genre> update(@PathVariable Long id, @RequestBody GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Жанр не найден"));

        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        genre.setPopularityIndex(request.getPopularityIndex());
        Genre saved = genreRepository.save(genre);
        logger.info("REST updated genre id={} name='{}'", saved.getId(), saved.getName());
        return ResponseEntity.ok(saved);
    }

    /**
     * Удаляет жанр по его идентификатору.
     *
     * @param id идентификатор жанра
     * @return ResponseEntity без тела (no content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Жанр не найден");
        }
        genreRepository.deleteById(id);
        logger.info("REST deleted genre id={}", id);
        return ResponseEntity.noContent().build();
    }
}
