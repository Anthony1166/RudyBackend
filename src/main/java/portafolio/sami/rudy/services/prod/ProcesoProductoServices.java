package portafolio.sami.rudy.services.prod;

import portafolio.sami.rudy.dto.prod.ProcesoProductoDTO;

import java.util.List;

public interface ProcesoProductoServices {
    List<ProcesoProductoDTO> listarPorProducto(Long productoId);
    ProcesoProductoDTO agregarAProducto(Long productoId, ProcesoProductoDTO procesoDTO);
    ProcesoProductoDTO actualizar(Long id, ProcesoProductoDTO procesoDTO);
    void eliminar(Long id);
    void reordenarLote(List<Long> idsOrdenados);
}
