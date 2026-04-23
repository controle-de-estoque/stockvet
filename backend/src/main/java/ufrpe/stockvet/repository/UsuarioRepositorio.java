package ufrpe.stockvet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import ufrpe.stockvet.models.Usuario;

public interface UsuarioRepositorio extends JpaRepository <Usuario, String>{

   UserDetails findByEmail(String email);

}
