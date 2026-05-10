package portafolio.sami.rudy.controllers.proy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.proy.ProyectoDTO;
import portafolio.sami.rudy.services.proy.ProyectoServices;

import java.util.List;

@RestController
@RequestMapping("/sami")
@RequiredArgsConstructor
public class ProyectoController {

    private final ProyectoServices proyectoServices;

    // --- PÚBLICO ---

    @GetMapping("/proyectos")
    public List<ProyectoDTO> listarActivos() {
        return proyectoServices.listarActivos();
    }

    @GetMapping("/proyecto/{id}")
    public ProyectoDTO obtenerPorId(@PathVariable Long id) {
        return proyectoServices.obtenerPorId(id);
    }

    @GetMapping("/proyecto/slug/{slug}")
    public ProyectoDTO obtenerPorSlug(@PathVariable String slug) {
        return proyectoServices.obtenerPorSlug(slug);
    }

    @GetMapping("/proyectos/buscar")
    public List<ProyectoDTO> buscarProyectos(@RequestParam(required = false, defaultValue = "") String termino) {
        return proyectoServices.buscarProyectos(termino);
    }

    // Ruta legacy mantenida para el frontend actual
    @GetMapping("/proyectoByCat/{categoriaId}")
    public List<ProyectoDTO> filtrarPorCategoriaLegacy(@PathVariable Long categoriaId) {
        return proyectoServices.listarPorCategoria(categoriaId);
    }

    @GetMapping("/proyectos/categoria/{categoriaId}")
    public List<ProyectoDTO> listarPorCategoria(@PathVariable Long categoriaId) {
        return proyectoServices.listarPorCategoria(categoriaId);
    }

    // Ruta legacy mantenida para el frontend actual
    @GetMapping("/proyectosAnio/{anio}")
    public List<ProyectoDTO> filtrarPorAnioLegacy(@PathVariable Integer anio) {
        return proyectoServices.listarPorAnio(anio);
    }

    @GetMapping("/proyectos/anio/{anio}")
    public List<ProyectoDTO> listarPorAnio(@PathVariable Integer anio) {
        return proyectoServices.listarPorAnio(anio);
    }

    // --- ADMIN ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/proyectos")
    public List<ProyectoDTO> listarTodosAdmin() {
        return proyectoServices.listarTodosAdmin();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/proyectos/categoria/{categoriaId}")
    public List<ProyectoDTO> listarPorCategoriaAdmin(@PathVariable Long categoriaId) {
        return proyectoServices.listarPorCategoriaAdmin(categoriaId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/proyecto")
    public ProyectoDTO registrarProyecto(@Valid @RequestBody ProyectoDTO proyectoDTO) {
        return proyectoServices.registrar(proyectoDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/proyecto/{id}")
    public ProyectoDTO actualizarProyecto(@PathVariable Long id, @Valid @RequestBody ProyectoDTO proyectoDTO) {
        return proyectoServices.actualizar(id, proyectoDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/proyecto/{id}/apagar")
    public void eliminarLogico(@PathVariable Long id) {
        proyectoServices.eliminarLogico(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/proyecto/{id}")
    public void eliminarFisico(@PathVariable Long id) {
        proyectoServices.eliminarFisico(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/proyectos/reordenar")
    public void reordenarLote(@RequestBody List<Long> idsOrdenados) {
        proyectoServices.reordenarLote(idsOrdenados);
    }
}
