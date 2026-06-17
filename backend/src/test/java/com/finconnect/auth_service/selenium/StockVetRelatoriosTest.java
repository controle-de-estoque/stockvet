package com.finconnect.auth_service.selenium;

import com.finconnect.auth_service.entity.Users;
import com.finconnect.auth_service.repository.UsersRepository;
import com.finconnect.auth_service.repository.MovimentacaoRepository;
import com.finconnect.auth_service.service.ProductsService;
import com.finconnect.auth_service.dto.SalvarEstoque;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Duration;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=8080")
public class StockVetRelatoriosTest {

    private WebDriver driver;
    private StockVetPage loginPage;        
    private StockVetRelatoriosPage page;   
    private String baseUrl;
    private UUID uuidEstoqueTeste; // Guardará o ID do estoque para injetar no navegador

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private PasswordEncoder encoder;

    @MockitoBean 
    private ProductsService productsService;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        
        loginPage = new StockVetPage(driver);
        page = new StockVetRelatoriosPage(driver);
        baseUrl = "http://localhost:4200";

        // --- SEED DO BANCO DE DADOS (Igual ao do Matheus) ---
        movimentacaoRepository.deleteAll();
        usersRepository.deleteAll();

        uuidEstoqueTeste = UUID.randomUUID();
        when(productsService.salvarEstoque(any(SalvarEstoque.class))).thenReturn(uuidEstoqueTeste);

        Users userLogin = new Users();
        userLogin.setFirstName("Usuario");
        userLogin.setLastName("Valido");
        userLogin.setEmail("usuario.valido@teste.com");
        userLogin.setPassword(encoder.encode("senha123"));
        userLogin.setFirstPetName("Rex");
        userLogin.setEstoque(uuidEstoqueTeste); // Vincula o estoque ao usuário
        userLogin.setAdmin(true);
        usersRepository.save(userLogin);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Nested
    @DisplayName("Funcionalidade - Relatórios de Movimentação")
    class RelatoriosTests {

        @BeforeEach
        void logarENavegarParaRelatorios() throws InterruptedException {
            driver.get(baseUrl + "/login");
            loginPage.preencherLogin("usuario.valido@teste.com", "senha123");
            loginPage.clicarBotaoLogin();
            Thread.sleep(2000);

            // 🔥 INJEÇÃO PRO CONTEXTO DO ANGULAR: Garante que o localStorage tenha o ID do estoque
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("localStorage.setItem('estoqueId', '" + uuidEstoqueTeste.toString() + "');");
            js.executeScript("localStorage.setItem('estoque', '" + uuidEstoqueTeste.toString() + "');");
            Thread.sleep(2000);

            driver.get(baseUrl + "/relatorios");
            Thread.sleep(1500); // Um pouco mais de tempo para o ApexCharts renderizar
        }

        @Test
        @DisplayName("CT-REL-01: Gráficos são exibidos automaticamente ao entrar na tela")
        void ctRel01() {
            Assertions.assertTrue(page.graficosEstaoVisiveis(),
                "Os gráficos deveriam renderizar automaticamente com base no dataFinal = hoje");
        }

        @Test
        @DisplayName("CT-REL-02: Estado padrão exibe exatamente os últimos 6 meses a partir de hoje")
        void ctRel02() throws InterruptedException {
            page.aguardarCarregamentoGraficos();
            int meses = page.contarMesesNoGrafico();
            Assertions.assertEquals(6, meses,
                "O estado padrão deveria exibir exatamente 6 meses, mas exibiu: " + meses);
        }

        @Test
        @DisplayName("CT-REL-03: Data final alterada desloca a janela de 6 meses corretamente")
        void ctRel03() throws InterruptedException {
            page.preencherDataFinal("2026-03-31");
            String ultimoMes = page.obterLabelUltimoMesGrafico();
            Assertions.assertTrue(
                ultimoMes.toLowerCase().contains("mar"),
                "Ao definir dataFinal em março, o último mês do gráfico deveria ser mar 2026, mas foi: " + ultimoMes
            );
        }

        @Test
        @DisplayName("CT-REL-04: Data inicial como limitador reduz a janela exibida no gráfico")
        void ctRel04() throws InterruptedException {
            java.time.LocalDate umMesAtras = java.time.LocalDate.now().minusMonths(1).withDayOfMonth(1);
            page.preencherDataInicial(umMesAtras.toString());
            int meses = page.contarMesesNoGrafico();
            Assertions.assertTrue(meses <= 2,
                "Com dataInicial no mês passado, o gráfico deveria exibir no máximo 2 meses, mas exibiu: " + meses);
        }

        @Test
        @DisplayName("CT-REL-05: Data inicial e data final no mesmo mês exibe exatamente 1 mês")
        void ctRel05() throws InterruptedException {
            page.preencherDataInicial("2026-05-01");
            page.preencherDataFinal("2026-05-31");
            int meses = page.contarMesesNoGrafico();
            Assertions.assertEquals(1, meses,
                "Com início e fim no mesmo mês, o gráfico deveria exibir exatamente 1 mês");
        }

        @Test
        @DisplayName("CT-REL-06: Tentar baixar relatório sem tipo e formato exibe alerta de validação")
        void ctRel06() throws InterruptedException {
            page.clicarBaixarRelatorio();
            Thread.sleep(500);
            String alerta = page.obterTextoDoAlertaNativo();
            Assertions.assertEquals(
                "Por favor, selecione o tipo de relatório e o formato do arquivo.",
                alerta
            );
            page.aceitarAlerta();
        }

        @Test
        @DisplayName("CT-REL-07: Baixar histórico de movimentações em PDF com período definido")
        void ctRel07() throws InterruptedException {
            page.selecionarTipoRelatorio("geral");
            page.selecionarFormatoRelatorio("pdf");
            page.preencherDataInicial("2026-01-01");
            page.preencherDataFinal("2026-06-30");
            page.clicarBaixarRelatorio();
            Thread.sleep(2000);
            Assertions.assertEquals(baseUrl + "/relatorios", driver.getCurrentUrl());
        }

        @Test
        @DisplayName("CT-REL-08: Baixar relatório de consumo por período em XLSX")
        void ctRel08() throws InterruptedException {
            page.selecionarTipoRelatorio("entrada");
            page.selecionarFormatoRelatorio("xlsx");
            page.preencherDataInicial("2026-03-01");
            page.preencherDataFinal("2026-06-30");
            page.clicarBaixarRelatorio();
            Thread.sleep(2000);
            Assertions.assertEquals(baseUrl + "/relatorios", driver.getCurrentUrl());
        }

        @Test
        @DisplayName("CT-REL-09: Relatório de vencimento cobre os 2 meses seguintes à data inicial")
        void ctRel09() throws InterruptedException {
            page.selecionarTipoRelatorio("vencimento");
            page.selecionarFormatoRelatorio("pdf");
            page.preencherDataInicial("2026-06-01");
            page.clicarBaixarRelatorio();
            Thread.sleep(2000);
            Assertions.assertEquals(baseUrl + "/relatorios", driver.getCurrentUrl(),
                "O relatório de vencimento para 2 meses seguintes deveria ser gerado sem erros");
        }

        @Test
        @DisplayName("CT-REL-10: Data final como limitador no vencimento restringe a janela para 1 mês seguinte")
        void ctRel10() throws InterruptedException {
            page.selecionarTipoRelatorio("vencimento");
            page.selecionarFormatoRelatorio("xlsx");
            page.preencherDataInicial("2026-06-01");
            page.preencherDataFinal("2026-07-31");
            page.clicarBaixarRelatorio();
            Thread.sleep(2000);
            Assertions.assertEquals(baseUrl + "/relatorios", driver.getCurrentUrl(),
                "O relatório de vencimento limited a 1 mês seguinte deveria ser gerado sem erros");
        }
    }
}