package portafolio.sami.rudy.controllers.proy;

import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/sami")
@RequiredArgsConstructor
public class ImagenProyectoController {

    private final ImagenProyectoServices imagenServices;
    private final S3Service s3Service;

    // --- PÚBLICO ---

    @GetMapping("/proyecto/{proyectoId}/imagenes")
    public List<ImagenProyectoDTO> listarPorProyecto(@PathVariable Long proyectoId) {
        return imagenServices.listarPorProyecto(proyectoId);
    }

    // --- ADMIN (estilo Producto: URL en JSON body) ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/proyecto/{proyectoId}/imagen")
    public ImagenProyectoDTO agregarImagen(@PathVariable Long proyectoId,
                                           @Valid @RequestBody ImagenProyectoDTO imagenDTO) {
        return imagenServices.agregarAProyecto(proyectoId, imagenDTO);
    }

    // Upload de archivo — sube a S3 y luego registra vía agregarAProyecto
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/proyecto/{proyectoId}/imagen/upload")
    public ResponseEntity<ImagenProyectoDTO> uploadImagen(@PathVariable Long proyectoId,
                                                          @RequestParam("file") MultipartFile archivo,
                                                          @RequestParam(value = "textoAlternativo", required = false) String textoAlternativo) throws IOException {
        String url = s3Service.uploadFile(archivo);
        ImagenProyectoDTO dto = new ImagenProyectoDTO();
        dto.setUrlImagen(url);
        dto.setTextoAlternativo(textoAlternativo);
        return ResponseEntity.ok(imagenServices.agregarAProyecto(proyectoId, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/imagen-proyecto/{id}")
    public ImagenProyectoDTO actualizarImagen(@PathVariable Long id,
                                              @Valid @RequestBody ImagenProyectoDTO imagenDTO) {
        return imagenServices.actualizar(id, imagenDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/imagen-proyecto/{id}")
    public void eliminarImagen(@PathVariable Long id) {
        imagenServices.eliminar(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/imagenes-proyecto/reordenar")
    public void reordenarLote(@RequestBody List<Long> idsOrdenados) {
        imagenServices.reordenarLote(idsOrdenados);
    }
}
