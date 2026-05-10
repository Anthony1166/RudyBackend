package portafolio.sami.rudy.controllers.proy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.proy.CategoriaProyectoDTO;
import portafolio.sami.rudy.services.proy.CategoriaProyectoServices;

import java.util.List;

@RestController
@RequestMapping("/sami")
@RequiredArgsConstructor
public class CategoriaProyectoController {

    private final CategoriaProyectoServices categoriaServices;

    // --- PÚBLICO ---

    @GetMapping("/categorias-proy/publicas")
    public List<CategoriaProyectoDTO> listarPublicas() {
        return categoriaServices.listarPublicas();
    }

    @GetMapping("/categorias-proy")
    public List<CategoriaProyectoDTO> listarTodas() {
        return categoriaServices.listarTodas();
    }

    @GetMapping("/categorias-proy/{id}")
    public CategoriaProyectoDTO obtenerPorId(@PathVariable Long id) {
        return categoriaServices.obtenerPorId(id);
    }

    @GetMapping("/categorias-proy/buscar")
    public List<CategoriaProyectoDTO> buscarCategorias(@RequestParam(defaultValue = "") String termino) {
        return categoriaServices.buscarCategorias(termino);
    }

    // --- PRIVADO (Panel de Control) ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/categoria-proy")
    public CategoriaProyectoDTO guardar(@Valid @RequestBody CategoriaProyectoDTO categoriaDTO) {
        return categoriaServices.guardar(categoriaDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/categoria-proy/{id}")
    public CategoriaProyectoDTO actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaProyectoDTO categoriaDTO) {
        return categoriaServices.actualizar(id, categoriaDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/categoria-proy/{id}/apagar")
    public void eliminarLogico(@PathVariable Long id) {
        categoriaServices.eliminarLogico(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/categoria-proy/{id}")
    public void eliminarFisico(@PathVariable Long id) {
        categoriaServices.eliminarFisico(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/categorias-proy/reordenar")
    public void reordenarLote(@RequestBody List<Long> idsOrdenados) {
        categoriaServices.reordenarLote(idsOrdenados);
    }
}
