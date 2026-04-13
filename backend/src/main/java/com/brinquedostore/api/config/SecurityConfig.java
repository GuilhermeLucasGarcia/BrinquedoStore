package com.brinquedostore.api.config;

import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.model.PerfilUsuario;
import com.brinquedostore.api.repository.IntegranteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/", "/login", "/register", "/error", "/catalogo/**", "/detalhes/**", "/sobre", "/carrinho/**", "/api/search/public", "/css/**", "/img/**", "/js/**").permitAll()
                        .antMatchers("/dashboard").authenticated()
                        .antMatchers("/api/search/admin").hasAnyRole("FUNCIONARIO", "ADMIN")
                        .antMatchers("/administracao/**").hasAnyRole("FUNCIONARIO", "ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .successHandler((request, response, authentication) -> {
                            boolean funcionario = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> "ROLE_FUNCIONARIO".equals(authority.getAuthority())
                                            || "ROLE_ADMIN".equals(authority.getAuthority()));
                            response.sendRedirect(funcionario ? "/administracao/catalogos" : "/dashboard");
                        })
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            String usuario = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonimo";
                            logger.warn("Tentativa de acesso negado. usuario={}, uri={}, metodo={}, ip={}",
                                    usuario, request.getRequestURI(), request.getMethod(), request.getRemoteAddr());
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        })
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            ObjectProvider<IntegranteRepository> integranteRepositoryProvider,
            @Value("${app.admin.username:admin}") String adminUsername,
            @Value("${app.admin.password:admin123}") String adminPassword,
            PasswordEncoder passwordEncoder) {
        return username -> {
            IntegranteRepository integranteRepository = integranteRepositoryProvider.getIfAvailable();
            if (integranteRepository != null) {
                Integrante integrante = integranteRepository.findByNomeUsuarioIgnoreCase(username)
                        .orElseGet(() -> integranteRepository.findByEmailIgnoreCase(username).orElse(null));
                if (integrante != null) {
                    return toUserDetails(integrante);
                }
            }

            if (adminUsername.equalsIgnoreCase(username)) {
                return User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .roles("ADMIN", "FUNCIONARIO")
                        .build();
            }

            throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuário não encontrado: " + username);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private UserDetails toUserDetails(Integrante integrante) {
        PerfilUsuario perfil = integrante.getPerfil() != null ? integrante.getPerfil() : PerfilUsuario.CLIENTE;
        return User.builder()
                .username(integrante.getNomeUsuario())
                .password(integrante.getSenha() != null ? integrante.getSenha() : "")
                .roles(perfil.name())
                .build();
    }
}
