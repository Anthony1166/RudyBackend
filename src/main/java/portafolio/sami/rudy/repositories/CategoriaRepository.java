package portafolio.sami.rudy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import portafolio.sami.rudy.entities.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
