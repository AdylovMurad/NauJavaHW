package ru.murad.SmartLibrary;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfSystemProperty(named = "ui.tests", matches = "true")
public class LoginUiTest extends BookGeneratorTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        super.setUp();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
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
        String afterLoginContent = login("admin", "adminpass");
        Assertions.assertNotNull(afterLoginContent);
        Assertions.assertTrue(driver.getCurrentUrl().contains("/ui/books"));

        String afterLogoutContent = logout();
        Assertions.assertNotNull(afterLogoutContent);
        Assertions.assertTrue(driver.getCurrentUrl().contains("login?logout") ||
                afterLogoutContent.contains("Вы успешно вышли из системы"));
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

        var loginButton = driver.findElement(By.className("btn-primary"));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/login?error"));

        var pageContent = driver.getPageSource();
        Assertions.assertTrue(pageContent.contains("Неверное имя пользователя или пароль"));
    }

    private String login(String user, String pass) {
        driver.get(baseUrl + "/login");

        var usernameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("username"))
        );
        usernameField.sendKeys(user);

        var passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(pass);

        var loginButton = driver.findElement(By.className("btn-primary"));
        loginButton.click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        return driver.getPageSource();
    }

    private String logout() {
        driver.get(baseUrl + "/ui/profile");

        var logoutButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("form[action='/logout'] button"))
        );
        logoutButton.click();

        wait.until(ExpectedConditions.urlContains("/login?logout"));

        return driver.getPageSource();
    }
}
