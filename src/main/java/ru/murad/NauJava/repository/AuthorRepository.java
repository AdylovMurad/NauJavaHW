package ru.murad.NauJava.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.murad.NauJava.entity.Author;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
}
