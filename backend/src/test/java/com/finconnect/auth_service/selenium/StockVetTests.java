package com.finconnect.auth_service.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StockVetTests {

    static WebDriver driver;

    static final String URL = "http://localhost:4200";
    static final String EMAIL = "teste@teste.com";
    static final String SENHA = "12345678";

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false
        ));
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterAll
    static void teardown() {
        driver.quit();
    }

    void fazerLogin() throws InterruptedException {
        driver.get(URL + "/login");
        driver.findElement(By.id("username")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.xpath("//button[text()='Login']")).click();
        Thread.sleep(4000);
    }

    // CT-01: Cadastrar cessionário com nome e e-mail válidos
    @Test @Order(1)
    void CT01_cadastrarCessionarioValido() throws InterruptedException {
        fazerLogin();
        driver.get(URL + "/cadastrar-cessionario");
        Thread.sleep(1000);
        driver.findElement(By.id("nome")).sendKeys("Cessionario Novo");
        driver.findElement(By.id("email")).sendKeys("cessionarionovo@teste.com");
        driver.findElement(By.xpath("//button[contains(text(),'Cadastrar cessionário')]")).click();
        Thread.sleep(2000);
        try { driver.switchTo().alert().accept(); } catch (Exception e) {}
        Assertions.assertFalse(driver.getCurrentUrl().contains("erro"));
    }

    // CT-02: Tentar cadastrar cessionário com e-mail já existente
    @Test @Order(2)
    void CT02_cadastrarCessionarioEmailDuplicado() throws InterruptedException {
        driver.get(URL + "/cadastrar-cessionario");
        Thread.sleep(1000);
        driver.findElement(By.id("nome")).sendKeys("Cessionario Duplicado");
        driver.findElement(By.id("email")).sendKeys("cessionarionovo@teste.com");
        driver.findElement(By.xpath("//button[contains(text(),'Cadastrar cessionário')]")).click();
        Thread.sleep(2000);
        try { driver.switchTo().alert().accept(); } catch (Exception e) {}
        Assertions.assertTrue(driver.getCurrentUrl().contains("/cadastrar-cessionario"));
    }

    // CT-03: Cadastrar cessionário com nome em branco
    @Test @Order(3)
    void CT03_cadastrarCessionarioNomeBranco() throws InterruptedException {
        driver.get(URL + "/cadastrar-cessionario");
        Thread.sleep(1000);
        driver.findElement(By.id("email")).sendKeys("semNome@teste.com");
        driver.findElement(By.xpath("//button[contains(text(),'Cadastrar cessionário')]")).click();
        Thread.sleep(2000);
        try { driver.switchTo().alert().accept(); } catch (Exception e) {}
        Assertions.assertTrue(driver.getCurrentUrl().contains("/cadastrar-cessionario"));
    }

    // CT-04: Cadastrar cessionário com e-mail em branco
    @Test @Order(4)
    void CT04_cadastrarCessionarioEmailBranco() throws InterruptedException {
        driver.get(URL + "/cadastrar-cessionario");
        Thread.sleep(1000);
        driver.findElement(By.id("nome")).sendKeys("Sem Email");
        driver.findElement(By.xpath("//button[contains(text(),'Cadastrar cessionário')]")).click();
        Thread.sleep(2000);
        try { driver.switchTo().alert().accept(); } catch (Exception e) {}
        Assertions.assertTrue(driver.getCurrentUrl().contains("/cadastrar-cessionario"));
    }

    // CT-05: Cadastrar cessionário com e-mail inválido
    @Test @Order(5)
    void CT05_cadastrarCessionarioEmailInvalido() throws InterruptedException {
        driver.get(URL + "/cadastrar-cessionario");
        Thread.sleep(1000);
        driver.findElement(By.id("nome")).sendKeys("Email Invalido");
        driver.findElement(By.id("email")).sendKeys("emailinvalido");
        driver.findElement(By.xpath("//button[contains(text(),'Cadastrar cessionário')]")).click();
        Thread.sleep(2000);
        try { driver.switchTo().alert().accept(); } catch (Exception e) {}
        Assertions.assertTrue(driver.getCurrentUrl().contains("/cadastrar-cessionario"));
    }

    // CT-06: Verificar se cessionário aparece na listagem
    @Test @Order(6)
    void CT06_verificarCessionarioNaListagem() throws InterruptedException {
        driver.get(URL + "/admin");
        Thread.sleep(2000);
        String pagina = driver.getPageSource();
        Assertions.assertTrue(pagina.contains("Cessionario Novo"));
    }

    // CT-21: Registrar saída com dados válidos
    @Test @Order(7)
    void CT21_registrarSaidaValida() throws InterruptedException {
        fazerLogin();
        driver.get(URL + "/movimentacoes");
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("a[href='/movimentacoes/novo']")).click();
        Thread.sleep(3000);
        ((JavascriptExecutor) driver).executeScript(
                "var select = document.getElementById('tipo');" +
                        "select.value = select.options[2].value;" +
                        "select.dispatchEvent(new Event('change'));"
        );
        Thread.sleep(1000);
        Thread.sleep(1000);
        driver.findElement(By.id("data_movimentacao")).sendKeys("2026-06-03");
        driver.findElement(By.id("horario_movimentacao")).sendKeys("10:00");
        new Select(driver.findElement(By.id("cessionario"))).selectByIndex(1);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[contains(text(),'Adicionar produtos')]")).click();
        Thread.sleep(2000);
        Assertions.assertFalse(driver.getCurrentUrl().contains("erro"));
    }

    // CT-23: Tentar registrar saída sem selecionar cessionário
    @Test @Order(8)
    void CT23_registrarSaidaSemCessionario() throws InterruptedException {
        fazerLogin();
        driver.get(URL + "/movimentacoes");
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("a[href='/movimentacoes/novo']")).click();
        Thread.sleep(3000);
        ((JavascriptExecutor) driver).executeScript(
                "var select = document.getElementById('tipo');" +
                        "select.value = select.options[2].value;" +
                        "select.dispatchEvent(new Event('change'));"
        );
        Thread.sleep(1000);
        Thread.sleep(1000);
        driver.findElement(By.id("data_movimentacao")).sendKeys("2026-06-03");
        driver.findElement(By.id("horario_movimentacao")).sendKeys("10:00");
        driver.findElement(By.xpath("//button[contains(text(),'Adicionar produtos')]")).click();
        Thread.sleep(2000);
        try { driver.switchTo().alert().accept(); } catch (Exception e) {}
        Assertions.assertTrue(driver.getCurrentUrl().contains("/movimentacoes/novo"));
    }}