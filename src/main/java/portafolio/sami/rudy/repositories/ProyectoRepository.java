package portafolio.sami.rudy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import portafolio.sami.rudy.entities.Proyecto;

import java.util.List;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    // Buscamos proyectos donde la lista 'categorias' contenga una categoría con este ID
    List<Proyecto> findByCategoriasIdCategoria(Long categoriaId);

    List<Proyecto> findByAnio(Integer anio);
}
