package portafolio.sami.rudy.servicesImp;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.entities.ProcesoDiseno;
import portafolio.sami.rudy.entities.Proyecto;
import portafolio.sami.rudy.repositories.ProcesoDisenoRepository;
import portafolio.sami.rudy.repositories.ProyectoRepository;
import portafolio.sami.rudy.services.ProcesoDisenoServices;

import java.io.IOException;
import java.util.List;

@Service
public class ProcesoDisenoServicesImp implements ProcesoDisenoServices {

    @Autowired
    private ProcesoDisenoRepository procesoDisenoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private S3Service s3Service;

    @Override
    @Transactional // SOLUCIÓN: Mantiene la sesión de la BD abierta para cargar el campo @Lob
    public List<ProcesoDiseno> getAllProcesosByProyectoId(Long proyectoId) {
        return procesoDisenoRepository.findByProyectoIdProyectoOrderByOrdenAsc(proyectoId);
    }

    @Override
    public ProcesoDiseno createProceso(Long proyectoId, String tituloFase, String descripcion, Integer orden, MultipartFile imagen) throws IOException {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + proyectoId));

        ProcesoDiseno nuevoProceso = new ProcesoDiseno();
        nuevoProceso.setProyecto(proyecto);
        nuevoProceso.setTitulo_fase(tituloFase);
        nuevoProceso.setDescripcion(descripcion);
        nuevoProceso.setOrden(orden);

        if (imagen != null && !imagen.isEmpty()) {
            String imageUrl = s3Service.uploadFile(imagen);
            nuevoProceso.setImagen_proceso(imageUrl);
        }

        return procesoDisenoRepository.save(nuevoProceso);
    }

    @Override
    public ProcesoDiseno updateProceso(Long procesoId, String tituloFase, String descripcion, Integer orden, MultipartFile imagen) throws IOException {
        ProcesoDiseno procesoExistente = procesoDisenoRepository.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso de diseño no encontrado con ID: " + procesoId));

        procesoExistente.setTitulo_fase(tituloFase);
        procesoExistente.setDescripcion(descripcion);
        procesoExistente.setOrden(orden);

        if (imagen != null && !imagen.isEmpty()) {
            // Opcional: borrar la imagen antigua de S3 si existe
            if (procesoExistente.getImagen_proceso() != null) {
                // s3Service.deleteFile(procesoExistente.getImagen_proceso());
            }
            String imageUrl = s3Service.uploadFile(imagen);
            procesoExistente.setImagen_proceso(imageUrl);
        }

        return procesoDisenoRepository.save(procesoExistente);
    }

    @Override
    public void deleteProceso(Long procesoId) {
        ProcesoDiseno proceso = procesoDisenoRepository.findById(procesoId)
                .orElseThrow(() -> new RuntimeException("Proceso de diseño no encontrado con ID: " + procesoId));
        
        // Opcional: borrar la imagen de S3 antes de borrar la entidad
        if (proceso.getImagen_proceso() != null) {
            // s3Service.deleteFile(proceso.getImagen_proceso());
        }
        
        procesoDisenoRepository.deleteById(procesoId);
    }
}
