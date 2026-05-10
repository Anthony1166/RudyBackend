package portafolio.sami.rudy.controllers.proy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.dto.proy.ProcesoProyectoDTO;
import portafolio.sami.rudy.services.proy.ProcesoProyectoServices;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sami")
@RequiredArgsConstructor
public class ProcesoProyectoController {

    private final ProcesoProyectoServices procesoServices;
    private final S3Service s3Service;

    // --- PÚBLICO ---

    @GetMapping("/proyecto/{proyectoId}/procesos")
    public List<ProcesoProyectoDTO> listarPorProyecto(@PathVariable Long proyectoId) {
        return procesoServices.listarPorProyecto(proyectoId);
    }

    // --- ADMIN ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/proyecto/{proyectoId}/proceso")
    public ProcesoProyectoDTO agregarProceso(@PathVariable Long proyectoId,
                                              @Valid @RequestBody ProcesoProyectoDTO dto) {
        return procesoServices.agregarAProyecto(proyectoId, dto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/proyecto/{proyectoId}/proceso/upload")
    public ResponseEntity<ProcesoProyectoDTO> uploadProceso(@PathVariable Long proyectoId,
                                                             @RequestParam("titulo") String titulo,
                                                             @RequestParam("descripcion") String descripcion,
                                                             @RequestParam(value = "orden", required = false) Integer orden,
                                                             @RequestParam(value = "file", required = false) MultipartFile archivo) throws IOException {
        ProcesoProyectoDTO dto = new ProcesoProyectoDTO();
        dto.setTitulo(titulo);
        dto.setDescripcion(descripcion);
        dto.setOrden(orden);

        if (archivo != null && !archivo.isEmpty()) {
            dto.setUrlImagen(s3Service.uploadFile(archivo));
        }

        return ResponseEntity.ok(procesoServices.agregarAProyecto(proyectoId, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/proceso-proyecto/{id}")
    public ProcesoProyectoDTO actualizarProceso(@PathVariable Long id,
                                                 @Valid @RequestBody ProcesoProyectoDTO dto) {
        return procesoServices.actualizar(id, dto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/proceso-proyecto/{id}")
    public void eliminarProceso(@PathVariable Long id) {
        procesoServices.eliminar(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/procesos-proyecto/reordenar")
    public void reordenarLote(@RequestBody List<Long> idsOrdenados) {
        procesoServices.reordenarLote(idsOrdenados);
    }
}
