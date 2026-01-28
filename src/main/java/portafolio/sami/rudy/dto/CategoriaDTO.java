package portafolio.sami.rudy.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
// Ya no necesitamos la entidad Proyecto aquí
// import portafolio.sami.rudy.entities.Proyecto;

// import java.util.List;

@Getter
@Setter
public class CategoriaDTO {
    private Long idCategoria;
    private String nombre;

    // Esta línea es la que causa el bucle infinito en el JSON. La quitamos.
    // El endpoint para buscar proyectos por categoría ya se encarga de esa lógica.
    // private List<Proyecto> proyectos;
}
