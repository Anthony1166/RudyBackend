package portafolio.sami.rudy.services.prod;

import portafolio.sami.rudy.dto.prod.CategoriaProductoDTO;

import java.util.List;

public interface CategoriaProductoServices {
    List<CategoriaProductoDTO> listarTodas();
    List<CategoriaProductoDTO> listarPublicas();
    CategoriaProductoDTO obtenerPorId(Long id);
    CategoriaProductoDTO guardar(CategoriaProductoDTO categoriaDTO);
    CategoriaProductoDTO actualizar(Long id, CategoriaProductoDTO categoriaDTO);
    void eliminarLogico(Long id); // El que apaga
    void eliminarFisico(Long id); // El que destruye

    // 🔥 La búsqueda inteligente que le prometimos a Sami
    List<CategoriaProductoDTO> buscarCategorias(String termino);

    void reordenarLote(List<Long> idsOrdenados);
}
