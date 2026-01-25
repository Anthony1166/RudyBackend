package portafolio.sami.rudy.servicesImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import portafolio.sami.rudy.entities.Imagen;
import portafolio.sami.rudy.entities.Proyecto;
import portafolio.sami.rudy.repositories.ImagenRepository;
import portafolio.sami.rudy.repositories.ProyectoRepository;
import portafolio.sami.rudy.services.ImagenServices;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ImagenServicesImp implements ImagenServices{
    @Autowired
    private ImagenRepository imagenRepository;
    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private S3Service s3Service;

    @Override
    public Imagen subirImagen(MultipartFile file, String descripcion, Long idProyecto) throws IOException {
        // 1. Buscar el proyecto asociado (regla de negocio: toda imagen debe tener un proyecto)
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));

        // 2. Generar un nombre único para el archivo
        String nombreArchivo = UUID.randomUUID() + "_" + file.getOriginalFilename();

//        // 3. Crear ruta y guardar físicamente el archivo (persistencia lógica)
//        Path ruta = Paths.get("uploads").resolve(nombreArchivo);
//        Files.createDirectories(ruta.getParent());
//        Files.copy(file.getInputStream(), ruta);

        // Subir a S3 y obtener URL pública
        String urlImagen = s3Service.uploadFile(file);

        // 4. Construir y guardar la entidad Imagen (ensamblaje de datos + validación)
        Imagen imagen = new Imagen();
//        imagen.setImagen("/uploads/" + nombreArchivo);

        imagen.setImagen(urlImagen);

        imagen.setDescripcion(descripcion);
        imagen.setProyecto(proyecto);

        return imagenRepository.save(imagen);
    }

    @Override
    public List<Imagen> listarImagenes() {
        return imagenRepository.findAll();
    }

    @Override
    public Imagen buscarPorId(Long id) {
        return imagenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada"));
    }

    @Override
    public Imagen actualizarImagen(Long id, Imagen imagenActualizada) {
        Imagen imagen = buscarPorId(id);
        imagen.setDescripcion(imagenActualizada.getDescripcion());
        return imagenRepository.save(imagen);
    }

    @Override
    public void eliminarImagen(Long id) {
        Imagen imagen = buscarPorId(id);
        // Extraer el nombre del archivo de la URL de R2
        String url = imagen.getImagen();
        if (url != null && url.contains("/")) {
            // El nombre del archivo es lo que está después del último '/'
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            s3Service.deleteFile(fileName);
        }
        imagenRepository.delete(imagen);
    }
    @Override
    public List<Imagen> listarPorProyecto(Long proyectoId) {
        return imagenRepository.findByProyectoIdProyecto(proyectoId);
    }
}
