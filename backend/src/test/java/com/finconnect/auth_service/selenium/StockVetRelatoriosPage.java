package com.finconnect.auth_service.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class StockVetRelatoriosPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public StockVetRelatoriosPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void navegarParaRelatorios() {    
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(text(),'Relatórios')]")
        )).click();
    }

    public void selecionarTipoRelatorio(String valor) {    
        WebElement select = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tipo_relatorio")));    
        new Select(select).selectByValue(valor);
    }

    public void selecionarFormatoRelatorio(String valor) {    
        WebElement select = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("formato_arquivo")));    
        new Select(select).selectByValue(valor);
    }

    public void preencherDataInicial(String data) {
        limparEPreencher(By.id("data_inicial"), data);
        aguardarCarregamentoGraficos();
    }

    public void preencherDataFinal(String data) {
        limparEPreencher(By.id("data_final"), data);
        aguardarCarregamentoGraficos();
    }

    public void clicarBaixarRelatorio() {    
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(),'Baixar relatório')]")
        )).click();
    }

    public boolean graficosEstaoVisiveis() {    
        try {        
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".apexcharts-xaxis-texts-g tspan")
            ));        
            return true;    
        } catch (Exception e) {        
            return false;    
        }
    }

    public int contarMesesNoGrafico() {    
        // Pega os labels apenas do primeiro gráfico (entrada)
        List<WebElement> todosGraficos = driver.findElements(
            By.cssSelector(".apexcharts-canvas")
    );
        // Conta os labels apenas do primeiro canvas
        return todosGraficos.get(0).findElements(
            By.cssSelector(".apexcharts-xaxis-texts-g text")
    ).size();
    }

    public String obterLabelUltimoMesGrafico() {    
        List<WebElement> labels = driver.findElements(
            By.cssSelector(".apexcharts-xaxis-texts-g tspan")
        );    
        return labels.get(labels.size() - 1).getText();
    }

    public String obterLabelPrimeiroMesGrafico() {    
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".apexcharts-xaxis-texts-g text:first-child")
        )).getText();
    }

    public String obterTextoDoAlertaNativo() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    public void aceitarAlerta() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    private void limparEPreencher(By locator, String texto) {
        WebElement elemento = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    
    // Chrome precisa de tratamento especial para campos de data
    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].value = arguments[1];", elemento, texto
    );
    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", elemento
    );
    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", elemento
    );
    }

    public void aguardarCarregamentoGraficos() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".apexcharts-xaxis-texts-g tspan")
        ));
    }
}