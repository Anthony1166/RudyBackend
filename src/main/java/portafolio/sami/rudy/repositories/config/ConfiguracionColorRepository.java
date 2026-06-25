package portafolio.sami.rudy.repositories.config;

import org.springframework.data.jpa.repository.JpaRepository;
import portafolio.sami.rudy.entities.config.ConfiguracionColor;

import java.util.List;
import java.util.Optional;

public interface ConfiguracionColorRepository extends JpaRepository<ConfiguracionColor, Long> {

    Optional<ConfiguracionColor> findByClave(String clave);

    boolean existsByClave(String clave);

    List<ConfiguracionColor> findAllByOrderByIdAsc();
}
