package portafolio.sami.rudy.repositories.prod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import portafolio.sami.rudy.entities.prod.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findAllByActivoTrue();

    List<Producto> findAllByActivoTrueOrderByFechaCreacionDesc();

    List<Producto> findAllByActivoTrueOrderByOrdenAsc();

    List<Producto> findAllByOrderByOrdenAsc();


    Optional<Producto> findBySlug(String slug);

    List<Producto> findByCategorias_SlugAndActivoTrueOrderByOrdenAsc(String categoriaSlug);

    // 3. Herramientas para el Orden Automático y el Empuje
    @Query("SELECT COALESCE(MAX(p.orden), 0) FROM Producto p")
    Integer obtenerMaximoOrden();

    List<Producto> findByOrdenBetween(Integer inicio, Integer fin);


    // 🔥 EL NIVEL DIOS: Búsqueda difusa (Fuzzy Search)
    // Busca si el término se parece al nombre o a la descripción (umbral de similitud > 0.1)
    // Y además incluye ILIKE por si escribe solo una parte de la palabra.
    @Query(value = "SELECT * FROM productos WHERE activo = true AND " +
            "(nombre ILIKE %:termino% OR descripcion ILIKE %:termino% OR " +
            "similarity(nombre, :termino) > 0.1) " +
            "ORDER BY similarity(nombre, :termino) DESC", nativeQuery = true)
    List<Producto> buscarNivelDios(@Param("termino") String termino);

    // Para el panel de Admin: Trae todos (activos e inactivos) de una categoría
    @Query("SELECT p FROM Producto p JOIN p.categorias c WHERE c.id = :categoriaId ORDER BY p.orden ASC")
    List<Producto> findByCategoriaIdParaAdmin(@Param("categoriaId") Long categoriaId);
}
