package ru.murad.NauJava.repository;
import ru.murad.NauJava.entity.Loan;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "loans", path = "loans")
public interface LoanRepository extends CrudRepository<Loan, Long> {
}
