package ufrpe.stockvet.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ufrpe.stockvet.DTO.AutenticacaoDTO;
import ufrpe.stockvet.DTO.RecuperacaoSenhaDTO;
import ufrpe.stockvet.DTO.RegistroDTO;
import ufrpe.stockvet.DTO.RespostaLoginDTO;
import ufrpe.stockvet.infra.seguranca.ServicoToken;
import ufrpe.stockvet.models.Funcao;
import ufrpe.stockvet.models.Usuario;
import ufrpe.stockvet.repository.FuncaoRepositorio;
import ufrpe.stockvet.repository.UsuarioRepositorio;

import java.util.Optional;

@RestController
@RequestMapping("autent")

public class AutenticacaoController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepositorio repositorio;

    @Autowired
    private FuncaoRepositorio funcaoRepositorio;

    @Autowired
    private ServicoToken servicoToken;

    @PostMapping("/Login")
    public ResponseEntity<RespostaLoginDTO> login(@RequestBody @Valid AutenticacaoDTO data){
         var senhadoemail = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
         var autent = this.authenticationManager.authenticate(senhadoemail);

         var token = servicoToken.gerarToken((Usuario) autent.getPrincipal());

         return ResponseEntity.ok(new RespostaLoginDTO(token));

    }

    @PostMapping("/registrador")
    public ResponseEntity<Void> registrador(@RequestBody @Valid RegistroDTO data){
        if(this.repositorio.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String roleName = Optional.ofNullable(data.funcao())
                .map(RegistroDTO.FuncaoPayload::name)
                .filter(value -> !value.isBlank())
                .orElse("USUARIO");

        Funcao funcao = this.funcaoRepositorio
                .findByNameIgnoreCase(roleName)
                .orElseGet(() -> this.funcaoRepositorio.save(new Funcao(null, roleName.toUpperCase())));

        String senhaCriptografada = new BCryptPasswordEncoder().encode(data.senha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(data.email());
        novoUsuario.setSenha(senhaCriptografada);
        novoUsuario.setPrimeiroNome(data.nome());
        novoUsuario.setUltimoNome(data.sobrenome());
        novoUsuario.setPergunta(data.primeiroPet());
        novoUsuario.setEstoqueId(1);
        novoUsuario.setFuncao(funcao);


        this.repositorio.save(novoUsuario);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Void> recuperarSenha(@RequestBody @Valid RecuperacaoSenhaDTO data) {
        Usuario usuario = (Usuario) this.repositorio.findByEmail(data.email());
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        if (!usuario.getPergunta().equalsIgnoreCase(data.pergunta())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
