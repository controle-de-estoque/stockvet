package ufrpe.stockvet.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ufrpe.stockvet.DTO.Dados;
import ufrpe.stockvet.models.Usuario;
import ufrpe.stockvet.service.UsuarioService;


@RestController
@RequestMapping ("/login")
public class LoginController {

    private final UsuarioService service;

    public LoginController(UsuarioService usuarioService){
        this.service = usuarioService;
    }

    @PostMapping("/cadastrar-usuario")
    public Usuario cadastrarUsuario(@RequestBody Dados user){
        return service.salvarUsuario(user);
    }

}