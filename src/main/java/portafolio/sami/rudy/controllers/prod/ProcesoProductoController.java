package portafolio.sami.rudy.controllers.prod;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.prod.ProcesoProductoDTO;
import portafolio.sami.rudy.services.prod.ProcesoProductoServices;

import java.util.List;

@RestController
@RequestMapping("/sami")
public class ProcesoProductoController {
    @Autowired
    private ProcesoProductoServices procesoServices;

    // --- PÚBLICO ---

    @GetMapping("/producto/{productoId}/procesos")
    public List<ProcesoProductoDTO> listarPorProducto(@PathVariable Long productoId) {
        return procesoServices.listarPorProducto(productoId);
    }

    // --- PRIVADO ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/producto/{productoId}/proceso")
    public ProcesoProductoDTO agregarProceso(@PathVariable Long productoId, @Valid @RequestBody ProcesoProductoDTO procesoDTO) {
        return procesoServices.agregarAProducto(productoId, procesoDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/proceso-producto/{id}")
    public ProcesoProductoDTO actualizarProceso(@PathVariable Long id, @Valid @RequestBody ProcesoProductoDTO procesoDTO) {
        return procesoServices.actualizar(id, procesoDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/proceso-producto/{id}")
    public void eliminarProceso(@PathVariable Long id) {
        procesoServices.eliminar(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/procesos-producto/reordenar")
    public void reordenarLote(@RequestBody List<Long> idsOrdenados) {
        procesoServices.reordenarLote(idsOrdenados);
    }
}
