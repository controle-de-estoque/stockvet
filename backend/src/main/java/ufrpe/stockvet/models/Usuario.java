package ufrpe.stockvet.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "Usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "email")


public class Usuario implements UserDetails {

    @Id
    private String email;

    @Column(name = "primeiro_nome", nullable = false)
    private String primeiroNome;

    @Column(name = "ultimo_nome", nullable = false)
    private String ultimoNome;

    @Column(name = "pergunta", nullable = false)
    private String pergunta;

    @Column(name = "senha", nullable = false)
    private String senha;

    @ManyToOne
    @JoinColumn(name = "funcao_id")
    private Funcao funcao;

    @Column(name = "estoque_id", nullable = false)
    private int estoqueId;

    public Usuario(String email, String senha, Funcao funcao){
        this.email = email;
        this.senha = senha;
        this.funcao = funcao;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.funcao == null || this.funcao.getName() == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));
        }

        if (this.funcao.getName().equals("ADMIN"))
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USUARIO"),
                    new SimpleGrantedAuthority("ROLE_ESTUDANTE")
            );
        else if (this.funcao.getName().equals("USUARIO"))
            return List.of(
                    new SimpleGrantedAuthority("ROLE_USUARIO")
            );
        else
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ESTUDANTE")
            );
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
