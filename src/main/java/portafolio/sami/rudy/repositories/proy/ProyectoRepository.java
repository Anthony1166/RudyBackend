package portafolio.sami.rudy.repositories.proy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import portafolio.sami.rudy.entities.proy.Proyecto;

import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    // --- Listados públicos (solo activos) ---
    @Query("SELECT p FROM Proyecto p WHERE p.activo = true ORDER BY p.anio DESC, p.orden ASC")
    List<Proyecto> findAllActivosOrdenados();

    List<Proyecto> findAllByOrderByOrdenAsc();

    Optional<Proyecto> findBySlug(String slug);

    // Filtra por categoría (público — solo activos)
    List<Proyecto> findByCategorias_IdCategoriaAndActivoTrue(Long categoriaId);

    // Filtra por año (público — solo activos)
    List<Proyecto> findByAnioAndActivoTrue(Integer anio);

    // --- Para el panel admin (todos, activos e inactivos) ---
    @Query("SELECT p FROM Proyecto p JOIN p.categorias c WHERE c.idCategoria = :categoriaId ORDER BY p.orden ASC")
    List<Proyecto> findByCategoriaIdParaAdmin(@Param("categoriaId") Long categoriaId);

    // --- Utilidades de orden ---
    @Query("SELECT COALESCE(MAX(p.orden), 0) FROM Proyecto p")
    Integer obtenerMaximoOrden();

    // --- Búsqueda fuzzy con pg_trgm ---
    @Query(value = "SELECT * FROM proyecto WHERE activo = true AND " +
            "(titulo ILIKE CONCAT('%', :termino, '%') OR descripcion ILIKE CONCAT('%', :termino, '%') OR " +
            "similarity(titulo, :termino) > 0.1) " +
            "ORDER BY similarity(titulo, :termino) DESC", nativeQuery = true)
    List<Proyecto> buscarNivelDios(@Param("termino") String termino);
}
