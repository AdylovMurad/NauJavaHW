package ru.murad.NauJava.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.murad.NauJava.entity.Author;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "authors", path = "authors")
public interface AuthorRepository extends org.springframework.data.repository.CrudRepository<Author, Long> {
	Optional<Author> findByFullName(String fullName);
}
