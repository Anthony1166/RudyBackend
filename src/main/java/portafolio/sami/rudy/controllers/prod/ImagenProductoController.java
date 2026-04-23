package portafolio.sami.rudy.controllers.prod;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.prod.ImagenProductoDTO;
import portafolio.sami.rudy.services.prod.ImagenProductoServices;

import java.util.List;

@RestController
@RequestMapping("/sami")
public class ImagenProductoController {
    @Autowired
    private ImagenProductoServices imagenServices;

    // --- PÚBLICO ---

    // Angular usará esto para pintar la galería en la página del producto
    @GetMapping("/producto/{productoId}/imagenes")
    public List<ImagenProductoDTO> listarPorProducto(@PathVariable Long productoId) {
        return imagenServices.listarPorProducto(productoId);
    }

    // --- PRIVADO (Panel de Control) ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/producto/{productoId}/imagen")
    public ImagenProductoDTO agregarImagen(@PathVariable Long productoId, @Valid @RequestBody ImagenProductoDTO imagenDTO) {
        return imagenServices.agregarAProducto(productoId, imagenDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/imagen-producto/{id}")
    public ImagenProductoDTO actualizarImagen(@PathVariable Long id, @Valid @RequestBody ImagenProductoDTO imagenDTO) {
        return imagenServices.actualizar(id, imagenDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/imagen-producto/{id}")
    public void eliminarImagen(@PathVariable Long id) {
        imagenServices.eliminar(id);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/imagenes-producto/reordenar")
    public void reordenarLote(@RequestBody List<Long> idsOrdenados) {
        imagenServices.reordenarLote(idsOrdenados);
    }
}
