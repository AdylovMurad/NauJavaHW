package ru.murad.NauJava;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.murad.NauJava.entity.Book;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest extends BookGeneratorTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setupRestAssured() {
        RestAssured.port = port;
    }

    @Test
    public void testGetBookById() {
        Book expectedBook = books.get(0);

        Book responseBook = given()
                .auth().preemptive().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .pathParam("id", expectedBook.getId())
                .when()
                .get("/api/books/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(Book.class);

        Assertions.assertEquals(expectedBook.getTitle(), responseBook.getTitle());
        Assertions.assertEquals(expectedBook.getIsbn(), responseBook.getIsbn());
        Assertions.assertEquals(1869, responseBook.getPublicationYear());
    }

    @Test
    public void testGetBookByIdNotFound() {
        given()
                .auth().preemptive().basic("admin", "admin")
                .pathParam("id", 99999)
                .when()
                .get("/api/books/{id}")
                .then()
                .log().ifValidationFails()
                .statusCode(404);
    }

    @Test
    public void testFilterBooks() {
        given()
                .auth().preemptive().basic("admin", "admin")
                .queryParam("title", "Война")
                .queryParam("start", 1800)
                .queryParam("end", 1900)
                .when()
                .get("/api/books/filter")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].title", containsString("Война"))
                .body("[0].author.fullName", equalTo("Лев Толстой"));
    }

    @Test
    public void testGetBooksByAuthor() {
        given()
                .auth().preemptive().basic("admin", "admin")
                .queryParam("name", "Лев Толстой")
                .when()
                .get("/api/books/by-author")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].title", equalTo("Война и мир"));
    }

    @Test
    public void testNoAuthRedirect() {
        given()
                .pathParam("id", books.get(0).getId())
                .when()
                .get("/api/books/{id}")
                .then()
                .statusCode(401);
    }
}