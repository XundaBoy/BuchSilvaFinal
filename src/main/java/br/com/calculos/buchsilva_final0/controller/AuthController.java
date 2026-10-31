
package br.com.calculos.buchsilva_final0.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.calculos.buchsilva_final0.entity.Usuario;
import br.com.calculos.buchsilva_final0.repository.UsuarioRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;

    // TOKEN VÁLIDO POR 30 MINUTOS
    private static final long DURACAO_TOKEN = 1800;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            UsuarioRepository usuarioRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    // DADOS QUE O ANGULAR ENVIA
    public record LoginRequest(
        @NotBlank String login,
        @NotBlank String senha
    ) {}

    // DADOS QUE O BACKEND DEVOLVE
    public record LoginResponse(
        String token,
        String tipo,
        long expiraEmSegundos,
        String nome,
        String email,
        String perfil
    ) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest dados) {

        Authentication autenticacao;

        try {

            autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    dados.login(),
                    dados.senha()
                )
            );

        } catch (AuthenticationException e) {

            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "E-mail ou senha inválidos"
            );
        }

        // USUÁRIO AUTENTICADO NO BANCO
        Usuario usuario = usuarioRepository
            .findByLogin(autenticacao.getName())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não encontrado"
                )
            );

        // PERMISSÕES DO USUÁRIO
        List<String> permissoes = autenticacao
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        Instant agora = Instant.now();

        // DADOS DO JWT
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("buchsilva-final")
            .subject(usuario.getLogin())
            .issuedAt(agora)
            .expiresAt(
                agora.plusSeconds(DURACAO_TOKEN)
            )
            .claim("roles", permissoes)
            .build();

        JwsHeader header = JwsHeader
            .with(MacAlgorithm.HS256)
            .build();

        String token = jwtEncoder.encode(
            JwtEncoderParameters.from(
                header,
                claims
            )
        ).getTokenValue();

        // DEVOLVE NOME E PERFIL PARA O ANGULAR
        return ResponseEntity.ok(
            new LoginResponse(
                token,
                "Bearer",
                DURACAO_TOKEN,
                usuario.getNome(),
                usuario.getLogin(),
                usuario.getPerfil().name()
            )
        );
    }
}
