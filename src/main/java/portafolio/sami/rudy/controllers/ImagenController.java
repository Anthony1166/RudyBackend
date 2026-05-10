package portafolio.sami.rudy.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.dto.proy.ImagenProyectoDTO;
import portafolio.sami.rudy.services.proy.ImagenProyectoServices;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.io.IOException;
import java.util.List;

// Endpoints legacy — mantenidos para compatibilidad con el frontend actual (Phase 5 los migrará)
@RestController
@RequestMapping("/sami")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenProyectoServices imagenServices;
    private final S3Service s3Service;

    // Sube el archivo a S3, luego registra la URL en la BD
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/imagen")
    public ResponseEntity<ImagenProyectoDTO> subirImagen(@RequestParam("file") MultipartFile archivo,
                                                         @RequestParam("descripcion") String descripcion,
                                                         @RequestParam("idProyecto") Long idProyecto) throws IOException {
        String url = s3Service.uploadFile(archivo);
        ImagenProyectoDTO dto = new ImagenProyectoDTO();
        dto.setUrlImagen(url);
        dto.setTextoAlternativo(descripcion);
        return ResponseEntity.ok(imagenServices.agregarAProyecto(idProyecto, dto));
    }

    @GetMapping("/imagen/{id}")
    public ImagenProyectoDTO buscarPorId(@PathVariable Long id) {
        return imagenServices.obtenerPorId(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/imagen/{id}")
    public ImagenProyectoDTO actualizarImagen(@PathVariable Long id,
                                              @RequestBody ImagenProyectoDTO imagenDTO) {
        return imagenServices.actualizar(id, imagenDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/imagen/{id}")
    public void eliminarImagen(@PathVariable Long id) {
        imagenServices.eliminar(id);
    }

    @GetMapping("/proyectoimg/{proyectoId}")
    public List<ImagenProyectoDTO> listarImagenesPorProyecto(@PathVariable Long proyectoId) {
        return imagenServices.listarPorProyecto(proyectoId);
    }
}
