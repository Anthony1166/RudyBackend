package portafolio.sami.rudy.repositories.prod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import portafolio.sami.rudy.entities.prod.CategoriaProducto;

import java.util.List;
import java.util.Optional;

public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {

    boolean existsByNombre(String nombre);

    // Para buscar por la URL amigable
    Optional<CategoriaProducto> findBySlug(String slug);

    // Panel de Sami: Trae TODAS (activas e inactivas) ordenadas
    List<CategoriaProducto> findAllByOrderByOrdenAsc();

    // EL MENÚ INTELIGENTE: Trae solo las ACTIVAS que tienen productos ACTIVOS
    @Query("SELECT DISTINCT c FROM CategoriaProducto c JOIN c.productos p WHERE p.activo = true AND c.activo = true ORDER BY c.orden ASC")
    List<CategoriaProducto> buscarCategoriasConProductosActivos();

    // Búsqueda Nivel Dios (ahora solo busca entre las activas)
    @Query(value = "SELECT * FROM categorias_producto WHERE activo = true AND " +
            "(nombre ILIKE %:termino% OR similarity(nombre, :termino) > 0.2) " +
            "ORDER BY similarity(nombre, :termino) DESC", nativeQuery = true)
    List<CategoriaProducto> buscarCategoriaNivelDios(@Param("termino") String termino);

    // Herramientas para el Orden Automático y el Empuje
    @Query("SELECT COALESCE(MAX(c.orden), 0) FROM CategoriaProducto c")
    Integer obtenerMaximoOrden();

    List<CategoriaProducto> findByOrdenBetween(Integer inicio, Integer fin);
    
}
