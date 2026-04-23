package ufrpe.stockvet.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import ufrpe.stockvet.DTO.Dados;
import ufrpe.stockvet.exceptions.usuario.EmailInvalidoException;
import ufrpe.stockvet.exceptions.usuario.NomeInvalidoException;
import ufrpe.stockvet.exceptions.usuario.PerguntaInvalidaException;
import ufrpe.stockvet.exceptions.usuario.SenhaInvalidaException;
import ufrpe.stockvet.models.Usuario;
import ufrpe.stockvet.repository.UsuarioRepositorio;

@Service
public class UsuarioService{

    private final UsuarioRepositorio repositorio;

    public UsuarioService(UsuarioRepositorio repositorio){
        this.repositorio = repositorio;
    }


    public Usuario salvarUsuario(@NotNull Dados novoUsuario){

        if(novoUsuario.getEmail() == null){
            throw new EmailInvalidoException();
        }
        if(novoUsuario.getPrimeiroNome() == null || novoUsuario.getUltimoNome() == null){
            throw new NomeInvalidoException();
        }
        if(novoUsuario.getSenha() == null){
            throw new SenhaInvalidaException();
        }
        if(novoUsuario.getPergunta() == null){
            throw new PerguntaInvalidaException();
        }

        Usuario user = new Usuario();

        user.setEmail(novoUsuario.getEmail());
        user.setPrimeiroNome(novoUsuario.getPrimeiroNome());
        user.setUltimoNome(novoUsuario.getUltimoNome());
        user.setSenha(novoUsuario.getSenha());
        user.setPergunta(novoUsuario.getPergunta());
        user.setEstoqueId(novoUsuario.getEstoqueId());

        return repositorio.save(user);
    }

    public Usuario buscarUsuario(@NotNull String email){
        return repositorio.findById(email).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
    }

    public void deletarUsuario (@NotNull Dados user){
        repositorio.deleteById(user.getEmail());
    }

    public Usuario redefinirSenhaUsuario(@NotNull String email, @NotNull String novaSenha){
        Usuario user = buscarUsuario(email);

        user.setSenha(novaSenha);

        return repositorio.save(user);
    }

}
