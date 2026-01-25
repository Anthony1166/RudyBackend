package portafolio.sami.rudy.services;

import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.entities.Imagen;

import java.io.IOException;
import java.util.List;

public interface ImagenServices {
    Imagen subirImagen(MultipartFile file, String descripcion, Long idProyecto) throws IOException;
    List<Imagen> listarImagenes();
    Imagen buscarPorId(Long id);
    Imagen actualizarImagen(Long id, Imagen imagenActualizada);
    void eliminarImagen(Long id);
    List<Imagen> listarPorProyecto(Long idProyecto);
}
