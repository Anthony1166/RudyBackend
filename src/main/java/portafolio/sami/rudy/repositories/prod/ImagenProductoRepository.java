package portafolio.sami.rudy.repositories.prod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import portafolio.sami.rudy.entities.prod.ImagenProducto;

import java.util.List;
import java.util.Optional;

public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {
    // Magia de Spring: "Encuentra por el ID del producto y ordénalo de menor a mayor"
    // Lista todas las fotos de un producto ordenadas

    List<ImagenProducto> findByProductoIdOrderByOrdenAsc(Long productoId);

    // Busca si ese producto ya tiene una foto marcada como portada
    Optional<ImagenProducto> findByProductoIdAndEsPortadaTrue(Long productoId);

    // Herramientas para el Orden Aislado
    @Query("SELECT COALESCE(MAX(i.orden), 0) FROM ImagenProducto i WHERE i.producto.id = :productoId")
    Integer obtenerMaximoOrdenPorProducto(@Param("productoId") Long productoId);

    // Herramienta para el Empuje (Shift) dentro de un mismo producto
    List<ImagenProducto> findByProductoIdAndOrdenBetween(Long productoId, Integer inicio, Integer fin);
}
