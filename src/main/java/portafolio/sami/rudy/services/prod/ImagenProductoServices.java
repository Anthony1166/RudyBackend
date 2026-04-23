package portafolio.sami.rudy.services.prod;

import portafolio.sami.rudy.dto.prod.ImagenProductoDTO;

import java.util.List;

public interface ImagenProductoServices {
    List<ImagenProductoDTO> listarPorProducto(Long productoId);
    ImagenProductoDTO agregarAProducto(Long productoId, ImagenProductoDTO imagenDTO);
    ImagenProductoDTO actualizar(Long id, ImagenProductoDTO imagenDTO);
    void eliminar(Long id); // Aquí usamos borrado físico porque las fotos se descartan
    void reordenarLote(List<Long> idsOrdenados);
}
