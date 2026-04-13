package ru.murad.NauJava;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginUiTest extends BookGeneratorTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        super.setUp();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginAndLogout() {
        String afterLoginContent = login("admin", "admin");
        Assertions.assertNotNull(afterLoginContent);
        Assertions.assertTrue(driver.getCurrentUrl().equals(baseUrl + "/") || afterLoginContent.contains("api"));


        String afterLogoutContent = logout();
        Assertions.assertNotNull(afterLogoutContent);
        Assertions.assertTrue(afterLogoutContent.contains("You have been signed out")
                || driver.getCurrentUrl().contains("login?logout"));
    }

    @Test
    public void testLoginInvalidCredentials() {
        driver.get(baseUrl + "/login");

        var usernameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username"))
        );
        usernameField.sendKeys("wrongUser");

        var passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("wrongPass");

        var loginButton = driver.findElement(By.className("primary"));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/login?error"));

        var pageContent = driver.getPageSource();
        Assertions.assertTrue(pageContent.contains("Invalid credentials")
                || pageContent.contains("Bad credentials"));
    }

    private String login(String user, String pass) {
        driver.get(baseUrl + "/login");

        var usernameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username"))
        );
        usernameField.sendKeys(user);

        var passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(pass);

        var loginButton = driver.findElement(By.className("primary"));
        loginButton.click();

        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));

        return driver.getPageSource();
    }

    private String logout() {
        driver.get(baseUrl + "/logout");

        var logoutButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.className("primary"))
        );
        logoutButton.click();

        wait.until(ExpectedConditions.urlContains("/login?logout"));

        return driver.getPageSource();
    }
}