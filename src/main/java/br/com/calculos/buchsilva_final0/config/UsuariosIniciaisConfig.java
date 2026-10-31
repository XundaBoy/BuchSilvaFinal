
package br.com.calculos.buchsilva_final0.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.calculos.buchsilva_final0.entity.Usuario;
import br.com.calculos.buchsilva_final0.repository.UsuarioRepository;

@Configuration
public class UsuariosIniciaisConfig {

    @Bean
    public CommandLineRunner criarUsuariosIniciais(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,

            @Value("${app.usuarios.admin.senha}")
            String senhaAdmin,

            @Value("${app.usuarios.visualizador.senha}")
            String senhaVisualizador) {

        return args -> {

            // JACQUELINE - ADMINISTRADORA
            cadastrarSeNaoExiste(
                repository,
                passwordEncoder,
                "Jacqueline Buch",
                "jacquelinebuch@hotmail.com",
                senhaAdmin,
                Usuario.Perfil.ADMIN
            );

            // SANDRO - VISUALIZADOR
            cadastrarSeNaoExiste(
                repository,
                passwordEncoder,
                "Sandro Silva",
                "sandroluizdasilva@hotmail.com",
                senhaVisualizador,
                Usuario.Perfil.VISUALIZADOR
            );
        };
    }

    private void cadastrarSeNaoExiste(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            String nome,
            String email,
            String senha,
            Usuario.Perfil perfil) {

        // NÃO CRIA USUÁRIOS REPETIDOS
        if (repository.findByLogin(email).isPresent()) {
            return;
        }

        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException(
                "Senha não configurada para " + email
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setLogin(email);

        // SALVA O HASH DA SENHA
        usuario.setSenhaHash(
            passwordEncoder.encode(senha)
        );

        usuario.setPerfil(perfil);

        repository.save(usuario);
    }
}
