package com.finconnect.auth_service.selenium;

import com.finconnect.auth_service.entity.Users;
import com.finconnect.auth_service.repository.UsersRepository;
import com.finconnect.auth_service.service.ProductsService;
import com.finconnect.auth_service.dto.SalvarEstoque;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Novo Import
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Duration;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=8080")
public class StockVetE2ETest {

    private WebDriver driver;
    private StockVetPage page;
    private String baseUrl;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder encoder;

    @MockitoBean // Substituído aqui
    private ProductsService productsService;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        page = new StockVetPage(driver);
        baseUrl = "http://localhost:4200";

        usersRepository.deleteAll();

        UUID estoqueId = UUID.randomUUID();
        when(productsService.salvarEstoque(any(SalvarEstoque.class))).thenReturn(estoqueId);

        Users userLogin = new Users();
        userLogin.setFirstName("Usuario");
        userLogin.setLastName("Valido");
        userLogin.setEmail("usuario.valido@teste.com");
        userLogin.setPassword(encoder.encode("senha123"));
        userLogin.setFirstPetName("Rex");
        userLogin.setEstoque(estoqueId);
        userLogin.setAdmin(true);
        usersRepository.save(userLogin);

        Users userRedefinir = new Users();
        userRedefinir.setFirstName("Usuario");
        userRedefinir.setLastName("Cadastrado");
        userRedefinir.setEmail("usuario.cadastrado@teste.com");
        userRedefinir.setPassword(encoder.encode("senhaAntiga"));
        userRedefinir.setFirstPetName("Rex");
        userRedefinir.setEstoque(estoqueId);
        userRedefinir.setAdmin(true);
        usersRepository.save(userRedefinir);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Nested
    @DisplayName("Funcionalidade - Cadastro")
    class CadastroTests {

        @BeforeEach
        void navegar() {
            driver.get(baseUrl + "/cadastro");
        }

        @Test
        @DisplayName("CT-02-01: Cadastro com sucesso")
        void ct0201() {
            page.preencherCadastro("João", "Silva", "joao@teste.com", "senha12345", "Rex");
            page.clicarBotaoCadastro();
            assertEquals("Cadastro realizado com sucesso!", page.obterTextoDoAlertaNativo());
        }
    }

    @Nested
    @DisplayName("Funcionalidade - Redefinir Senha")
    class RedefinirSenhaTests {

        @BeforeEach
        void navegar() {
            driver.get(baseUrl + "/redefinir-senha");
        }

        @Test
        @DisplayName("CT-03-01: Redefinir senha com sucesso")
        void ct0301() {
            page.preencherRedefinicao("usuario.cadastrado@teste.com", "Rex", "novaSenha123");
            page.clicarBotaoRedefinir();
            assertEquals("Senha redefinida com sucesso!", page.obterTextoDoAlertaNativo());
        }

        @Test
        @DisplayName("CT-03-02: Usuário não está cadastrado no sistema")
        void ct0302() {
            page.preencherRedefinicao("nao.cadastrado@teste.com", "Rex", "novaSenha123");
            page.clicarBotaoRedefinir();
            assertEquals("E-mail ou resposta da pergunta de segurança incorretos.", page.obterErroGlobal());
        }

        @Test
        @DisplayName("CT-03-03: Redefinir senha com email inválido")
        void ct0303() {
            page.preencherRedefinicao("invalido.com", "Rex", "novaSenha123");
            page.clicarBotaoRedefinir();
            assertEquals("Verifique todos os campos antes de submeter", page.obterErroGlobal());
            assertEquals("Este não é um email válido", page.obterErroDoCampo("email"));
        }

        @Test
        @DisplayName("CT-03-04: Redefinir senha com email vazio")
        void ct0304() {
            page.preencherRedefinicao("", "Rex", "novaSenha123");
            page.clicarBotaoRedefinir();
            assertEquals("Verifique todos os campos antes de submeter", page.obterErroGlobal());
            assertEquals("Campo obrigatório", page.obterErroDoCampo("email"));
        }

        @Test
        @DisplayName("CT-03-05: Redefinir senha com senha vazia")
        void ct0305() {
            page.preencherRedefinicao("usuario.cadastrado@teste.com", "Rex", "");
            page.clicarBotaoRedefinir();
            assertEquals("Verifique todos os campos antes de submeter", page.obterErroGlobal());
            assertEquals("Campo obrigatório", page.obterErroDoCampo("senha"));
        }

        @Test
        @DisplayName("CT-03-06: Redefinir senha com senha < 8 digitos")
        void ct0306() {
            page.preencherRedefinicao("usuario.cadastrado@teste.com", "Rex", "inval");
            page.clicarBotaoRedefinir();
            assertEquals("Verifique todos os campos antes de submeter", page.obterErroGlobal());
            assertEquals("A senha deve conter pelo menos 8 caracteres", page.obterErroDoCampo("senha"));
        }

        @Test
        @DisplayName("CT-03-07: Redefinir senha com nome do pet vazio")
        void ct0307() {
            page.preencherRedefinicao("usuario.cadastrado@teste.com", "", "novaSenha123");
            page.clicarBotaoRedefinir();
            assertEquals("Verifique todos os campos antes de submeter", page.obterErroGlobal());
            assertEquals("Campo obrigatório", page.obterErroDoCampo("animalEstimacao"));
        }

        @Test
        @DisplayName("CT-03-08: Redefinir senha com nome do pet incorreto")
        void ct0308() {
            page.preencherRedefinicao("usuario.cadastrado@teste.com", "incorreto", "novaSenha123");
            page.clicarBotaoRedefinir();
            assertEquals("E-mail ou resposta da pergunta de segurança incorretos.", page.obterErroGlobal());
        }

        @Test
        @DisplayName("CT-03-09: Redefinir senha com nome do pet em case diferente")
        void ct0309() {
            page.preencherRedefinicao("usuario.cadastrado@teste.com", "CASEDIFERENTE", "novaSenha123");
            page.clicarBotaoRedefinir();
            assertEquals("E-mail ou resposta da pergunta de segurança incorretos.", page.obterErroGlobal());
        }

        @Test
        @DisplayName("CT-03-10: Redefinir senha com email em case diferente")
        void ct0310() {
            page.preencherRedefinicao("CASEDIFERENTE@TESTE.COM", "Rex", "novaSenha123");
            page.clicarBotaoRedefinir();
            assertEquals("E-mail ou resposta da pergunta de segurança incorretos.", page.obterErroGlobal());
        }
    }
}