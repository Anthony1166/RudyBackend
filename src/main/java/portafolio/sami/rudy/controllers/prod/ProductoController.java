package portafolio.sami.rudy.controllers.prod;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.prod.ProductoDTO;
import portafolio.sami.rudy.services.prod.ProductoServices;

import java.util.List;

@RestController
@RequestMapping("/sami")
public class ProductoController {
    @Autowired
    private ProductoServices productoServices;

    // --- PÚBLICO (Para la web de Figma) ---

    @GetMapping("/productos")
    public List<ProductoDTO> listarActivos() {
        return productoServices.listarActivos();
    }

    @GetMapping("/productos/nuevos")
    public List<ProductoDTO> listarNuevos() {
        return productoServices.listarNuevos();
    }

    @GetMapping("/producto/{id}")
    public ProductoDTO obtenerPorId(@PathVariable Long id) {
        return productoServices.obtenerPorId(id);
    }

    // --- PRIVADO (Para el Panel de Control) ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/productos")
    public List<ProductoDTO> listarTodosAdmin() {
        return productoServices.listarTodosAdmin();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/producto")
    public ProductoDTO registrarProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        return productoServices.registrar(productoDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/producto/{id}")
    public ProductoDTO actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDTO) {
        return productoServices.actualizar(id, productoDTO);
    }

    // 1. EL BOTÓN DE APAGAR (Borrado Lógico)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/producto/{id}/apagar")
    public void eliminarLogico(@PathVariable Long id) {
        productoServices.eliminarLogico(id);
    }

    // 2. EL BOTÓN DE DESTRUIR TOTALMENTE (Borrado Físico)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/producto/{id}")
    public void eliminarFisico(@PathVariable Long id) {
        productoServices.eliminarFisico(id);
    }

    @GetMapping("/productos/buscar")
    public List<ProductoDTO> buscarProductos(@RequestParam(required = false, defaultValue = "") String termino) {
        // Le ponemos required = false y defaultValue = "" para que, si Sami
        // borra todo el texto de la barra, el backend no tire error y
        // simplemente devuelva la lista completa por defecto.
        return productoServices.buscarProductos(termino);
    }

    // 🔥 EL NUEVO ENCHUFE 1: Para buscar un producto por su URL amigable
    // Usamos "/slug/{slug}" para que Spring Boot no lo confunda con el que busca por ID
    @GetMapping("/producto/slug/{slug}")
    public ProductoDTO obtenerPorSlug(@PathVariable String slug) {
        return productoServices.obtenerPorSlug(slug);
    }

    // 🔥 EL NUEVO ENCHUFE 2: ¡La respuesta a tu pregunta de los órdenes!
    // Trae los productos de una categoría específica, respetando el orden global
    @GetMapping("/productos/categoria/{categoriaSlug}")
    public List<ProductoDTO> listarPorCategoria(@PathVariable String categoriaSlug) {
        return productoServices.listarPorCategoria(categoriaSlug);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/productos/reordenar")
    public void reordenarLote(@RequestBody List<Long> idsOrdenados) {
        productoServices.reordenarLote(idsOrdenados);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/productos/categoria/{categoriaId}")
    public List<ProductoDTO> listarPorCategoriaAdmin(@PathVariable Long categoriaId) {
        return productoServices.listarPorCategoriaAdmin(categoriaId);
    }
}
