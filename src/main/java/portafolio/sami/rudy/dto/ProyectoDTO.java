package portafolio.sami.rudy.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProyectoDTO {
    private Long idProyecto;
    private String titulo;
    private String descripcion;
    private Integer anio;
    
    // Usamos DTOs en lugar de Entidades
    private List<CategoriaDTO> categorias;
    private List<ImagenDTO> imagenes;
}
