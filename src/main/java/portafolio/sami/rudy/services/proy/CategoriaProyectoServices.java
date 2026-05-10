package portafolio.sami.rudy.services.proy;

import portafolio.sami.rudy.dto.proy.CategoriaProyectoDTO;

import java.util.List;

public interface CategoriaProyectoServices {

    List<CategoriaProyectoDTO> listarTodas();

    List<CategoriaProyectoDTO> listarPublicas();

    CategoriaProyectoDTO obtenerPorId(Long id);

    CategoriaProyectoDTO guardar(CategoriaProyectoDTO categoriaDTO);

    CategoriaProyectoDTO actualizar(Long id, CategoriaProyectoDTO categoriaDTO);

    void eliminarLogico(Long id);

    void eliminarFisico(Long id);

    List<CategoriaProyectoDTO> buscarCategorias(String termino);

    void reordenarLote(List<Long> idsOrdenados);
}
