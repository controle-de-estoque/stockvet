package ufrpe.stockvet.infra.seguranca;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.auth0.jwt.JWT;
import ufrpe.stockvet.models.Usuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ServicoToken {

    @Value("${api.security.token.segredo}")
    private String segredo;

    public String gerarToken(Usuario usuario){
       try{
           Algorithm algoritmo = Algorithm.HMAC256(segredo);
           String token = JWT.create()
                   .withIssuer("stockvet")
                   .withSubject(usuario.getEmail())
                   .withExpiresAt(generateExpirationDate())
                   .sign(algoritmo);
           return token;

       }catch(JWTCreationException exception){
           throw new RuntimeException("Erro ao gerar o token", exception);
       }
    }

    public String validarToken(String token){
        try {
            Algorithm algoritmo = Algorithm.HMAC256(segredo);
            return JWT.require(algoritmo)
                    .withIssuer("stockvet")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }

    }


    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }
}
