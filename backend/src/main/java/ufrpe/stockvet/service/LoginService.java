package ufrpe.stockvet.service;

import org.springframework.stereotype.Service;
import ufrpe.stockvet.models.Usuario;

@Service
public class LoginService {

    private UsuarioService usuarioService;
    private Usuario usuarioLogado;
}
