package portafolio.sami.rudy.repositories.proy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import portafolio.sami.rudy.entities.proy.ImagenProyecto;

import java.util.List;
import java.util.Optional;

public interface ImagenProyectoRepository extends JpaRepository<ImagenProyecto, Long> {

    List<ImagenProyecto> findByProyectoIdProyectoOrderByOrdenAsc(Long proyectoId);

    Optional<ImagenProyecto> findByProyectoIdProyectoAndEsPortadaTrue(Long proyectoId);

    @Query("SELECT COALESCE(MAX(i.orden), 0) FROM ImagenProyecto i WHERE i.proyecto.idProyecto = :proyectoId")
    Integer obtenerMaximoOrdenPorProyecto(@Param("proyectoId") Long proyectoId);

    List<ImagenProyecto> findByProyectoIdProyectoAndOrdenBetween(Long proyectoId, Integer inicio, Integer fin);
}
