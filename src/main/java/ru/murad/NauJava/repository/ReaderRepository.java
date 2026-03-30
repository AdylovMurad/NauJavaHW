package ru.murad.NauJava.repository;
import ru.murad.NauJava.entity.Reader;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "readers", path = "readers")
public interface ReaderRepository extends CrudRepository<Reader, Long> {
}
