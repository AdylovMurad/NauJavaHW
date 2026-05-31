package ru.murad.SmartLibrary;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.murad.SmartLibrary.entity.Book;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest extends BookGeneratorTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setupRestAssured() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testGetBookById() {
        Book expectedBook = books.get(0);
        
        System.out.println("Testing GET /api/books/" + expectedBook.getId());

        Book responseBook = given()
                .auth().preemptive().basic("admin", "adminpass")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/books/{id}", expectedBook.getId())
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .as(Book.class);

        Assertions.assertEquals(expectedBook.getTitle(), responseBook.getTitle());
        Assertions.assertEquals(expectedBook.getIsbn(), responseBook.getIsbn());
        Assertions.assertEquals(expectedBook.getPublicationYear(), responseBook.getPublicationYear());
    }

    @Test
    public void testGetBookByIdNotFound() {
        given()
                .auth().preemptive().basic("admin", "adminpass")
                .when()
                .get("/api/books/{id}", 99999)
                .then()
                .log().ifValidationFails()
                .statusCode(404);
    }

    @Test
    public void testFilterBooks() {
        given()
                .auth().preemptive().basic("admin", "adminpass")
                .queryParam("title", "Война")
                .queryParam("start", 1800)
                .queryParam("end", 1900)
                .when()
                .get("/api/books/filter")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].title", containsString("Война"));
    }

    @Test
    public void testGetBooksByAuthor() {
        given()
                .auth().preemptive().basic("admin", "adminpass")
                .queryParam("name", "Лев Толстой")
                .when()
                .get("/api/books/by-author")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].author.fullName", equalTo("Лев Толстой"));
    }

    @Test
    public void testGetAllBooks() {
        given()
                .auth().preemptive().basic("admin", "adminpass")
                .when()
                .get("/api/books")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("size()", equalTo(3));
    }

    @Test
    public void testNoAuthRedirect() {
        given()
                .when()
                .get("/api/books/{id}", books.get(0).getId())
                .then()
                .statusCode(401);
    }
}