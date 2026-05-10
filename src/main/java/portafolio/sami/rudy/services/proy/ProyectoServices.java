package portafolio.sami.rudy.services.proy;

import portafolio.sami.rudy.dto.proy.ProyectoDTO;

import java.util.List;

public interface ProyectoServices {

    // --- Público ---
    List<ProyectoDTO> listarActivos();
    List<ProyectoDTO> listarPorAnio(Integer anio);
    ProyectoDTO obtenerPorId(Long id);
    ProyectoDTO obtenerPorSlug(String slug);
    List<ProyectoDTO> listarPorCategoria(Long categoriaId);
    List<ProyectoDTO> buscarProyectos(String termino);

    // --- Admin ---
    List<ProyectoDTO> listarTodosAdmin();
    List<ProyectoDTO> listarPorCategoriaAdmin(Long categoriaId);
    ProyectoDTO registrar(ProyectoDTO proyectoDTO);
    ProyectoDTO actualizar(Long id, ProyectoDTO proyectoDTO);
    void eliminarLogico(Long id);
    void eliminarFisico(Long id);
    void reordenarLote(List<Long> idsOrdenados);
}
