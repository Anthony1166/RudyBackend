package portafolio.sami.rudy.services;

import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.entities.ProcesoDiseno;

import java.io.IOException;
import java.util.List;

public interface ProcesoDisenoServices {

    List<ProcesoDiseno> getAllProcesosByProyectoId(Long proyectoId);

    ProcesoDiseno createProceso(Long proyectoId, String tituloFase, String descripcion, Integer orden, MultipartFile imagen) throws IOException;

    ProcesoDiseno updateProceso(Long procesoId, String tituloFase, String descripcion, Integer orden, MultipartFile imagen) throws IOException;

    void deleteProceso(Long procesoId);
}
