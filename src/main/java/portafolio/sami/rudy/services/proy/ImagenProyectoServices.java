package portafolio.sami.rudy.services.proy;

import portafolio.sami.rudy.dto.proy.ImagenProyectoDTO;

import java.util.List;

public interface ImagenProyectoServices {

    List<ImagenProyectoDTO> listarPorProyecto(Long proyectoId);

    ImagenProyectoDTO obtenerPorId(Long id);

    ImagenProyectoDTO agregarAProyecto(Long proyectoId, ImagenProyectoDTO imagenDTO);

    ImagenProyectoDTO actualizar(Long id, ImagenProyectoDTO imagenDTO);

    void eliminar(Long id);

    void reordenarLote(List<Long> idsOrdenados);
}
