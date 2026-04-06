package ru.murad.NauJava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.murad.NauJava.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}