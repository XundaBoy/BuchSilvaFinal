
package br.com.calculos.buchsilva_final0.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.calculos.buchsilva_final0.entity.Usuario;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

}
