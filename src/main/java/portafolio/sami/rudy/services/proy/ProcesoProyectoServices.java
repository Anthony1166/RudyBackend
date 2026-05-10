package portafolio.sami.rudy.services.proy;

import portafolio.sami.rudy.dto.proy.ProcesoProyectoDTO;

import java.util.List;

public interface ProcesoProyectoServices {

    List<ProcesoProyectoDTO> listarPorProyecto(Long proyectoId);

    ProcesoProyectoDTO obtenerPorId(Long id);

    ProcesoProyectoDTO agregarAProyecto(Long proyectoId, ProcesoProyectoDTO dto);

    ProcesoProyectoDTO actualizar(Long id, ProcesoProyectoDTO dto);

    void eliminar(Long id);

    void reordenarLote(List<Long> idsOrdenados);
}
