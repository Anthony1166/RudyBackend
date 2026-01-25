package portafolio.sami.rudy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import portafolio.sami.rudy.entities.Imagen;

import java.util.List;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByProyectoIdProyecto(Long idProyecto);
}
