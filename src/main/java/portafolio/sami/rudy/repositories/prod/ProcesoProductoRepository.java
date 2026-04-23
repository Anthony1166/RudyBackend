package portafolio.sami.rudy.repositories.prod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import portafolio.sami.rudy.entities.prod.ProcesoProducto;

import java.util.List;

public interface ProcesoProductoRepository extends JpaRepository<ProcesoProducto, Long> {


    // Trae el paso a paso ordenado para mostrar en la web
    List<ProcesoProducto> findByProductoIdOrderByOrdenAsc(Long productoId);

    // Magia del Orden
    @Query("SELECT COALESCE(MAX(p.orden), 0) FROM ProcesoProducto p WHERE p.producto.id = :productoId")
    Integer obtenerMaximoOrdenPorProducto(@Param("productoId") Long productoId);

    // Magia del Empuje
    List<ProcesoProducto> findByProductoIdAndOrdenBetween(Long productoId, Integer inicio, Integer fin);
}
