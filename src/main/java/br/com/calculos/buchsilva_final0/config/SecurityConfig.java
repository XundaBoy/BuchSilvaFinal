
package br.com.calculos.buchsilva_final0.config;

import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import br.com.calculos.buchsilva_final0.repository.UsuarioRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // =====================================
    // PROTEÇÃO DAS SENHAS
    // =====================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =====================================
    // BUSCAR USUÁRIO PELO E-MAIL
    // =====================================

    @Bean
    public UserDetailsService userDetailsService(
            UsuarioRepository repository) {

        return email -> {

            var usuario = repository.findByLogin(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Usuário não encontrado"
                    )
                );

            return User.withUsername(usuario.getLogin())
                .password(usuario.getSenhaHash())
                .roles(usuario.getPerfil().name())
                .build();
        };
    }

    // =====================================
    // VALIDAR LOGIN E SENHA
    // =====================================

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider(passwordEncoder);

        provider.setUserDetailsService(userDetailsService);

        return new ProviderManager(provider);
    }

    // =====================================
    // CHAVE SECRETA DO JWT
    // =====================================

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.jwt.secret-base64}")
            String secretBase64) {

        byte[] bytes =
            Base64.getDecoder().decode(secretBase64);

        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                "A chave JWT precisa ter pelo menos 32 bytes."
            );
        }

        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    // =====================================
    // GERAR TOKENS
    // =====================================

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {

        return new NimbusJwtEncoder(
            new ImmutableSecret<>(secretKey)
        );
    }

    // =====================================
    // VERIFICAR TOKENS
    // =====================================

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey) {

        var decoder = NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();

        decoder.setJwtValidator(
            JwtValidators.createDefaultWithIssuer(
                "buchsilva-final"
            )
        );

        return decoder;
    }

    // =====================================
    // RECONHECER O PERFIL DO JWT
    // =====================================

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        var authorities = new JwtGrantedAuthoritiesConverter();

        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");

        var converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
            authorities
        );

        return converter;
    }

    // =====================================
    // PERMITIR O ANGULAR
    // =====================================

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {

        var config = new CorsConfiguration();

        config.setAllowedOrigins(
            List.of("http://localhost:4200")
        );

        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        config.setAllowedHeaders(
            List.of("Authorization", "Content-Type")
        );

        var source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            config
        );

        return source;
    }

    // =====================================
    // REGRAS DE ACESSO À API
    // =====================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtConverter)
            throws Exception {

        http
            .cors(cors -> {})

            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .formLogin(AbstractHttpConfigurer::disable)

            .httpBasic(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(auth -> auth

                // QUALQUER PESSOA PODE TENTAR FAZER LOGIN
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/login"
                ).permitAll()

                // ADMIN E VISUALIZADOR PODEM CONSULTAR
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/entradas/**",
                    "/api/saidas/**",
                    "/api/saldo",
                    "/api/saldo/**"
                ).hasAnyRole(
                    "ADMIN",
                    "VISUALIZADOR"
                )

                // SOMENTE ADMIN PODE MODIFICAR DADOS
                .requestMatchers("/api/**")
                .hasRole("ADMIN")

                // NÃO LIBERAR OUTRAS ROTAS
                .anyRequest().denyAll()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(
                        jwtConverter
                    )
                )
            );

        return http.build();
    }
}
