package portafolio.sami.rudy.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.CategoriaDTO;
import portafolio.sami.rudy.entities.Categoria;
import portafolio.sami.rudy.services.CategoriaServices;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/sami")
public class CategoriaController {
    @Autowired
    private CategoriaServices categoriaServices;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/categorias")
    public List<CategoriaDTO> listarCategorias(){
        List<Categoria> categorias = categoriaServices.listarCategorias();
        List<CategoriaDTO> categoriasDTO = Arrays.asList(modelMapper.map(categorias, CategoriaDTO[].class));
        return categoriasDTO;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/categoria")
    public CategoriaDTO registraCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);
        categoria = categoriaServices.registerCategoria(categoria);
        categoriaDTO  = modelMapper.map(categoria, CategoriaDTO.class);
        return categoriaDTO;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/categoria/{id}")
    public CategoriaDTO actualizarCategoria(@PathVariable Long id ,@RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoria = modelMapper.map(categoriaDTO, Categoria.class);
        categoria=categoriaServices.actualizarCategoria(id, categoria);
        categoriaDTO  = modelMapper.map(categoria, CategoriaDTO.class);
        return categoriaDTO;
    }

    @GetMapping("/categoria/{id}")
    public CategoriaDTO buscarCategoriaId(@PathVariable Long id) {
        Categoria categoria = categoriaServices.buscarCategoriaId(id);
        CategoriaDTO categoriaDTO = modelMapper.map(categoria, CategoriaDTO.class);
        return categoriaDTO;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/categoria/{id}")
    public void eliminarCategoria(@PathVariable Long id) { categoriaServices.eliminarCategoria(id); }
}