package portafolio.sami.rudy.repositories.proy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import portafolio.sami.rudy.entities.proy.CategoriaProyecto;

import java.util.List;
import java.util.Optional;

public interface CategoriaProyectoRepository extends JpaRepository<CategoriaProyecto, Long> {

    boolean existsByNombre(String nombre);

    Optional<CategoriaProyecto> findBySlug(String slug);

    List<CategoriaProyecto> findAllByOrderByOrdenAsc();

    // Solo activas que tengan al menos un proyecto asociado
    @Query("SELECT DISTINCT c FROM CategoriaProyecto c JOIN c.proyectos p WHERE c.activo = true ORDER BY c.orden ASC")
    List<CategoriaProyecto> buscarCategoriasConProyectosActivos();

    // Búsqueda fuzzy con pg_trgm — solo entre activas
    @Query(value = "SELECT * FROM categoria WHERE activo = true AND " +
            "(nombre ILIKE CONCAT('%', :termino, '%') OR similarity(nombre, :termino) > 0.2) " +
            "ORDER BY similarity(nombre, :termino) DESC", nativeQuery = true)
    List<CategoriaProyecto> buscarCategoriaNivelDios(@Param("termino") String termino);

    @Query("SELECT COALESCE(MAX(c.orden), 0) FROM CategoriaProyecto c")
    Integer obtenerMaximoOrden();

    List<CategoriaProyecto> findByOrdenBetween(Integer inicio, Integer fin);
}
