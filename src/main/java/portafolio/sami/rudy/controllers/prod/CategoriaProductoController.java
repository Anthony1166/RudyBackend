package portafolio.sami.rudy.controllers.prod;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.prod.CategoriaProductoDTO;
import portafolio.sami.rudy.services.prod.CategoriaProductoServices;

import java.util.List;

@RestController
@RequestMapping("/sami")
public class CategoriaProductoController {


    @Autowired
    private CategoriaProductoServices categoriaServices;

    // --- PÚBLICO ---

    @GetMapping("/categorias-prod/publicas")
    public List<CategoriaProductoDTO> listarPublicas() {
        // Solo trae categorías que tienen productos y ordenadas bonito
        return categoriaServices.listarPublicas();
    }

    @GetMapping("/categorias-prod")
    public List<CategoriaProductoDTO> listarTodas() {
        return categoriaServices.listarTodas();
    }

    @GetMapping("/categorias-prod/{id}")
    public CategoriaProductoDTO obtenerPorId(@PathVariable Long id) {
        return categoriaServices.obtenerPorId(id);
    }

    @GetMapping("/categorias-prod/buscar")
    public List<CategoriaProductoDTO> buscarCategorias(@RequestParam(defaultValue = "") String termino) {
        return categoriaServices.buscarCategorias(termino);
    }

    // --- PRIVADO (Panel de Control) ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/categoria-prod")
    public CategoriaProductoDTO guardar(@Valid @RequestBody CategoriaProductoDTO categoriaDTO) {
        return categoriaServices.guardar(categoriaDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/categoria-prod/{id}")
    public CategoriaProductoDTO actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaProductoDTO categoriaDTO) {
        return categoriaServices.actualizar(id, categoriaDTO);
    }

    // 1. EL BOTÓN DE APAGAR (Borrado Lógico)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/categoria-prod/{id}/apagar")
    public void eliminarLogico(@PathVariable Long id) {
        categoriaServices.eliminarLogico(id);
    }

    // 2. EL BOTÓN DE DESTRUIR (Borrado Físico)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/categoria-prod/{id}")
    public void eliminarFisico(@PathVariable Long id) {
        categoriaServices.eliminarFisico(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/categorias-prod/reordenar")
    public void reordenarLote(@RequestBody List<Long> idsOrdenados) {
        categoriaServices.reordenarLote(idsOrdenados);
    }
}
