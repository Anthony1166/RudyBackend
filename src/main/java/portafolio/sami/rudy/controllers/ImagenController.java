package portafolio.sami.rudy.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import portafolio.sami.rudy.dto.ImagenDTO;
import portafolio.sami.rudy.entities.Imagen;
import portafolio.sami.rudy.services.ImagenServices;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sami")
public class ImagenController {
    @Autowired
    private ImagenServices imagenServices;
    @Autowired
    private ModelMapper modelMapper;

    // --- CORREGIDO ---
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/imagen")
    public ResponseEntity<ImagenDTO> subirImagen(@RequestParam("file") MultipartFile archivo,
                                                 @RequestParam("descripcion") String descripcion,
                                                 @RequestParam("idProyecto") Long idProyecto) throws IOException {
        Imagen imagen = imagenServices.subirImagen(archivo, descripcion, idProyecto);
        ImagenDTO dto = modelMapper.map(imagen, ImagenDTO.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/imagenes")
    public ResponseEntity<List<ImagenDTO>> obtenerTodasLasImagenes() {
        List<ImagenDTO> imagenes = imagenServices.listarImagenes()
                .stream()
                .map(img -> modelMapper.map(img, ImagenDTO.class))
                .toList();
        return ResponseEntity.ok(imagenes);
    }

    @GetMapping("/imagen/{id}")
    public ImagenDTO buscarPorId(@PathVariable Long id) {
        Imagen imagen = imagenServices.buscarPorId(id);
        return modelMapper.map(imagen, ImagenDTO.class);
    }

    // --- CORREGIDO ---
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/imagen/{id}")
    public ImagenDTO actualizarImagen(@PathVariable Long id, @RequestBody ImagenDTO imagenDTO) {
        Imagen imagenActualizada = modelMapper.map(imagenDTO, Imagen.class);
        Imagen imagen = imagenServices.actualizarImagen(id, imagenActualizada);
        return modelMapper.map(imagen, ImagenDTO.class);
    }

    // --- CORREGIDO ---
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/imagen/{id}")
    public void eliminarImagen(@PathVariable Long id) {
        imagenServices.eliminarImagen(id);
    }

    @GetMapping("/proyectoimg/{proyectoId}")
    public List<ImagenDTO> listarImagenesPorProyecto(@PathVariable Long proyectoId) {
        List<Imagen> imagenes = imagenServices.listarPorProyecto(proyectoId);
        return imagenes.stream()
                .map(img -> modelMapper.map(img, ImagenDTO.class))
                .toList();
    }
}