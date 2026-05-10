package portafolio.sami.rudy.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.dto.proy.ProcesoProyectoDTO;
import portafolio.sami.rudy.services.proy.ProcesoProyectoServices;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.io.IOException;
import java.util.List;

// Endpoints legacy — mantenidos para compatibilidad con el frontend actual (Phase 5 los migrará)
@RestController
@RequestMapping("/sami")
@RequiredArgsConstructor
public class ProcesoDisenoController {

    private final ProcesoProyectoServices procesoServices;
    private final S3Service s3Service;

    @GetMapping("/proceso-diseno/{proyectoId}")
    public ResponseEntity<List<ProcesoProyectoDTO>> obtenerProcesosPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(procesoServices.listarPorProyecto(proyectoId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/proceso-diseno/{proyectoId}")
    public ResponseEntity<ProcesoProyectoDTO> crearProceso(
            @PathVariable Long proyectoId,
            @RequestParam("titulo_fase") String tituloFase,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("orden") Integer orden,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        ProcesoProyectoDTO dto = new ProcesoProyectoDTO();
        dto.setTitulo(tituloFase);
        dto.setDescripcion(descripcion);
        dto.setOrden(orden);

        if (imagen != null && !imagen.isEmpty()) {
            dto.setUrlImagen(s3Service.uploadFile(imagen));
        }

        return new ResponseEntity<>(procesoServices.agregarAProyecto(proyectoId, dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/proceso-diseno/{procesoId}")
    public ResponseEntity<ProcesoProyectoDTO> actualizarProceso(
            @PathVariable Long procesoId,
            @RequestParam("titulo_fase") String tituloFase,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("orden") Integer orden,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        ProcesoProyectoDTO dto = new ProcesoProyectoDTO();
        dto.setTitulo(tituloFase);
        dto.setDescripcion(descripcion);
        dto.setOrden(orden);

        if (imagen != null && !imagen.isEmpty()) {
            dto.setUrlImagen(s3Service.uploadFile(imagen));
        }

        return ResponseEntity.ok(procesoServices.actualizar(procesoId, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/proceso-diseno/{procesoId}")
    public ResponseEntity<Void> eliminarProceso(@PathVariable Long procesoId) {
        procesoServices.eliminar(procesoId);
        return ResponseEntity.noContent().build();
    }
}
