package portafolio.sami.rudy.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.proy.CategoriaProyectoDTO;
import portafolio.sami.rudy.services.proy.CategoriaProyectoServices;

import java.util.List;

// Endpoints legacy — mantenidos para compatibilidad con el frontend actual (Phase 5 los migrará)
@RestController
@RequestMapping("/sami")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaProyectoServices categoriaServices;

    @GetMapping("/categorias")
    public List<CategoriaProyectoDTO> listarCategorias() {
        return categoriaServices.listarTodas();
    }

    @GetMapping("/categoria/{id}")
    public CategoriaProyectoDTO buscarCategoriaId(@PathVariable Long id) {
        return categoriaServices.obtenerPorId(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/categoria")
    public CategoriaProyectoDTO registraCategoria(@Valid @RequestBody CategoriaProyectoDTO categoriaDTO) {
        return categoriaServices.guardar(categoriaDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/categoria/{id}")
    public CategoriaProyectoDTO actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaProyectoDTO categoriaDTO) {
        return categoriaServices.actualizar(id, categoriaDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/categoria/{id}")
    public void eliminarCategoria(@PathVariable Long id) {
        categoriaServices.eliminarFisico(id);
    }
}
