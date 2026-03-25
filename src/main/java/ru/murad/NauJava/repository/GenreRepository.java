package ru.murad.NauJava.repository;
import ru.murad.NauJava.entity.Genre;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "genres", path = "genres")
public interface GenreRepository extends CrudRepository<Genre, Long> {
}
