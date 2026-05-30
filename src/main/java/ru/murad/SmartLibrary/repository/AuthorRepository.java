package ru.murad.SmartLibrary.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.murad.SmartLibrary.entity.Author;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "authors", path = "authors")
public interface AuthorRepository extends org.springframework.data.repository.CrudRepository<Author, Long> {
	Optional<Author> findByFullName(String fullName);
}
