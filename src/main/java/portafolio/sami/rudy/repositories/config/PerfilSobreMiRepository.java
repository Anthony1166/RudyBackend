package portafolio.sami.rudy.repositories.config;

import org.springframework.data.jpa.repository.JpaRepository;
import portafolio.sami.rudy.entities.config.PerfilSobreMi;

import java.util.Optional;

public interface PerfilSobreMiRepository extends JpaRepository<PerfilSobreMi, Long> {

    // El perfil es singleton: siempre trabajamos con el primer (y único) registro.
    Optional<PerfilSobreMi> findTopByOrderByIdAsc();
}
