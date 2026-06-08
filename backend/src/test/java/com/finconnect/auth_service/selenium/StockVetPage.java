package com.finconnect.auth_service.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class StockVetPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public StockVetPage(WebDriver driver) {
        this.driver = driver;
        // Configura uma espera explícita robusta de até 5 segundos
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    // --- Ações da Tela de Login ---
    public void preencherLogin(String email, String senha) {
        limparEPreencher(By.id("username"), email);
        limparEPreencher(By.id("password"), senha);
    }

    public void clicarBotaoLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Login']"))).click();
    }

    // --- Ações da Tela de Cadastro ---
    public void preencherCadastro(String nome, String sobrenome, String email, String senha, String pet) {
        limparEPreencher(By.id("nome"), nome);
        limparEPreencher(By.id("sobrenome"), sobrenome);
        limparEPreencher(By.id("email"), email);
        limparEPreencher(By.id("senha"), senha);
        limparEPreencher(By.id("animalEstimacao"), pet);
    }

    public void clicarBotaoCadastro() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Cadastro']"))).click();
    }

    // --- Ações da Tela de Redefinir Senha ---
    public void preencherRedefinicao(String email, String pet, String novaSenha) {
        limparEPreencher(By.id("email"), email);
        limparEPreencher(By.id("animalEstimacao"), pet);
        limparEPreencher(By.id("senha"), novaSenha);
    }

    public void clicarBotaoRedefinir() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='redefinir senha']"))).click();
    }

    // --- Métodos de Validação de Erros ---

    /**
     * Captura o erro global da página (gerado pelo errorMessage() fora do form)
     */
    public String obterErroGlobal() {
        By xpathGlobal = By.xpath("//form/following-sibling::p");
        WebElement erro = wait.until(ExpectedConditions.visibilityOfElementLocated(xpathGlobal));
        return erro.getText();
    }

    /**
     * Captura a mensagem de erro específica de um campo (ex: 'email', 'senha', 'animalEstimacao')
     */
    public String obterErroDoCampo(String idCampo) {
        By xpathCampo = By.xpath("//input[@id='" + idCampo + "']/following-sibling::p");
        WebElement erro = wait.until(ExpectedConditions.visibilityOfElementLocated(xpathCampo));
        return erro.getText();
    }

    /**
     * Captura o texto do alerta nativo javascript do navegador
     */
    public String obterTextoDoAlertaNativo() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    /**
     * Método auxiliar inteligente: Garante a visibilidade antes de limpar e preencher,
     * evitando o NoSuchElementException e o TimeoutException com o Angular assíncrono.
     */
    private void limparEPreencher(By locator, String texto) {
        WebElement elemento = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        elemento.clear();
        if (texto != null && !texto.isEmpty()) {
            elemento.sendKeys(texto);
        }
    }
}