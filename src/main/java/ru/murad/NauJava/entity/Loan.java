package ru.murad.NauJava.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_date")
    private LocalDateTime loanDate = LocalDateTime.now();

    @Column(name = "return_deadline")
    private LocalDateTime returnDeadline;

    private String status;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDateTime loanDate) { this.loanDate = loanDate; }
    public LocalDateTime getReturnDeadline() { return returnDeadline; }
    public void setReturnDeadline(LocalDateTime returnDeadline) { this.returnDeadline = returnDeadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }
}