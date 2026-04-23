package ufrpe.stockvet.infra.seguranca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ufrpe.stockvet.infra.seguranca.FiltroSeguranca;

import java.util.List;


@Configuration
@EnableWebSecurity



public class ConfigSeguranca {
    @Autowired
    private FiltroSeguranca FiltroSeguranca;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/autent/Login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/autent/registrador").permitAll()
                    .requestMatchers(HttpMethod.POST, "/autent/recuperar-senha").permitAll()


                        .requestMatchers(HttpMethod.POST, "/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasRole("ADMIN")


                        .requestMatchers(HttpMethod.POST, "/produtos").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers(HttpMethod.PUT, "/produtos/**").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers(HttpMethod.DELETE, "/produtos/**").hasAnyRole("ADMIN", "USUARIO")


                        .requestMatchers(HttpMethod.GET, "/produtos/**").hasAnyRole("ADMIN", "USUARIO", "ESTUDANTE")


                        .requestMatchers(HttpMethod.POST, "/kits").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers(HttpMethod.PUT, "/kits/**").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers(HttpMethod.GET, "/kits/**").hasAnyRole("ADMIN", "USUARIO", "ESTUDANTE")


                        .requestMatchers(HttpMethod.POST, "/transacoes/saida").hasAnyRole("ADMIN", "USUARIO", "ESTUDANTE")
                        .requestMatchers(HttpMethod.POST, "/transacoes/entrada").hasAnyRole("ADMIN", "USUARIO")


                        .requestMatchers(HttpMethod.GET, "/relatorios/**").hasAnyRole("ADMIN", "USUARIO")


                        .requestMatchers(HttpMethod.GET, "/unidades-medida").hasAnyRole("ADMIN", "USUARIO", "ESTUDANTE")
                        .requestMatchers(HttpMethod.POST, "/unidades-medida").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/unidades-medida/**").hasRole("ADMIN")





                        .anyRequest().authenticated()
                )
                .addFilterBefore(FiltroSeguranca, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();

    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
