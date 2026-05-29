package ru.murad.NauJava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.NauJava.controller.dto.GenreRequest;
import ru.murad.NauJava.entity.Genre;
import ru.murad.NauJava.exception.ResourceNotFoundException;
import ru.murad.NauJava.repository.GenreRepository;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreRestController {

    private final GenreRepository genreRepository;

    public GenreRestController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public ResponseEntity<List<Genre>> getAll() {
        return ResponseEntity.ok(genreRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getById(@PathVariable Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Жанр не найден"));
        return ResponseEntity.ok(genre);
    }

    @PostMapping
    public ResponseEntity<Genre> create(@RequestBody GenreRequest request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        genre.setPopularityIndex(request.getPopularityIndex());
        return ResponseEntity.ok(genreRepository.save(genre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Genre> update(@PathVariable Long id, @RequestBody GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Жанр не найден"));

        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        genre.setPopularityIndex(request.getPopularityIndex());
        return ResponseEntity.ok(genreRepository.save(genre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Жанр не найден");
        }
        genreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
