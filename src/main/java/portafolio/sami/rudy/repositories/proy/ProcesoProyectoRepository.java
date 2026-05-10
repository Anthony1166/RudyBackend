package portafolio.sami.rudy.repositories.proy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import portafolio.sami.rudy.entities.proy.ProcesoProyecto;

import java.util.List;

public interface ProcesoProyectoRepository extends JpaRepository<ProcesoProyecto, Long> {

    List<ProcesoProyecto> findByProyectoIdProyectoOrderByOrdenAsc(Long proyectoId);

    @Query("SELECT COALESCE(MAX(p.orden), 0) FROM ProcesoProyecto p WHERE p.proyecto.idProyecto = :proyectoId")
    Integer obtenerMaximoOrdenPorProyecto(@Param("proyectoId") Long proyectoId);

    List<ProcesoProyecto> findByProyectoIdProyectoAndOrdenBetween(Long proyectoId, Integer inicio, Integer fin);
}
