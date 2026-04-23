package portafolio.sami.rudy.services.prod;

import portafolio.sami.rudy.dto.prod.ProductoDTO;

import java.util.List;

public interface ProductoServices {
    // Para la web pública
    List<ProductoDTO> listarActivos();
    List<ProductoDTO> listarNuevos();
    ProductoDTO obtenerPorId(Long id);
    // Para el Panel de Control
    List<ProductoDTO> listarTodosAdmin();
    ProductoDTO registrar(ProductoDTO productoDTO);
    ProductoDTO actualizar(Long id, ProductoDTO productoDTO);
    void eliminarLogico(Long id);
    void eliminarFisico(Long id);

    List<ProductoDTO> buscarProductos(String termino);
    List<ProductoDTO> listarPorCategoria(String categoriaSlug);
    ProductoDTO obtenerPorSlug(String slug);

    void reordenarLote(List<Long> idsOrdenados);
    List<ProductoDTO> listarPorCategoriaAdmin(Long categoriaId);
}
