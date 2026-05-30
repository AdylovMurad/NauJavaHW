package ru.murad.SmartLibrary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.murad.SmartLibrary.entity.Genre;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "genres", path = "genres")
public interface GenreRepository extends JpaRepository<Genre, Long> {
	Optional<Genre> findByName(String name);
}
