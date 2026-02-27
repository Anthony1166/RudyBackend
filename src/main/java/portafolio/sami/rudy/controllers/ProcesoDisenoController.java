package portafolio.sami.rudy.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.dto.ProcesoDisenoDTO;
import portafolio.sami.rudy.entities.ProcesoDiseno;
import portafolio.sami.rudy.services.ProcesoDisenoServices;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sami")
public class ProcesoDisenoController {

    @Autowired
    private ProcesoDisenoServices procesoDisenoServices;

    @Autowired
    private ModelMapper modelMapper;

    // Endpoint público para obtener los procesos de un proyecto
    @GetMapping("/proceso-diseno/{proyectoId}")
    public ResponseEntity<List<ProcesoDisenoDTO>> obtenerProcesosPorProyecto(@PathVariable Long proyectoId) {
        List<ProcesoDiseno> procesos = procesoDisenoServices.getAllProcesosByProyectoId(proyectoId);
        List<ProcesoDisenoDTO> dtos = procesos.stream()
                .map(proceso -> modelMapper.map(proceso, ProcesoDisenoDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/proceso-diseno/{proyectoId}")
    public ResponseEntity<ProcesoDisenoDTO> crearProceso(
            @PathVariable Long proyectoId,
            @RequestParam("titulo_fase") String tituloFase,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("orden") Integer orden,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) throws IOException {
        
        ProcesoDiseno nuevoProceso = procesoDisenoServices.createProceso(proyectoId, tituloFase, descripcion, orden, imagen);
        ProcesoDisenoDTO dto = modelMapper.map(nuevoProceso, ProcesoDisenoDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/proceso-diseno/{procesoId}")
    public ResponseEntity<ProcesoDisenoDTO> actualizarProceso(
            @PathVariable Long procesoId,
            @RequestParam("titulo_fase") String tituloFase,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("orden") Integer orden,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        ProcesoDiseno procesoActualizado = procesoDisenoServices.updateProceso(procesoId, tituloFase, descripcion, orden, imagen);
        ProcesoDisenoDTO dto = modelMapper.map(procesoActualizado, ProcesoDisenoDTO.class);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/proceso-diseno/{procesoId}")
    public ResponseEntity<Void> eliminarProceso(@PathVariable Long procesoId) {
        procesoDisenoServices.deleteProceso(procesoId);
        return ResponseEntity.noContent().build();
    }
}
