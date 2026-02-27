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
    private String subtitulo;
    private String descripcion;
    private Integer anio;
    
    private List<CategoriaDTO> categorias;
    private List<ImagenDTO> imagenes;
    // Añadimos la lista de los nuevos DTOs
    private List<ProcesoDisenoDTO> procesosDiseno;
}
