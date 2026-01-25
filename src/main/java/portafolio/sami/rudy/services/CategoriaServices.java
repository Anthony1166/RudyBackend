package portafolio.sami.rudy.services;

import portafolio.sami.rudy.entities.Categoria;

import java.util.List;

public interface CategoriaServices {
    List<Categoria> listarCategorias();
    Categoria registerCategoria(Categoria categoria);
    Categoria actualizarCategoria(Long id, Categoria categoria);
    Categoria buscarCategoriaId(Long id);
    void eliminarCategoria(Long id);
}
