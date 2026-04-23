package portafolio.sami.rudy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoDTO {
    private Long idProyecto;
    private String titulo;
    private String subtitulo;
    private String descripcion;
    private Integer anio;
    
    private List<CategoriaDTO> categorias;
    private List<ImagenDTO> imagenes;
    private List<ProcesoDisenoDTO> procesosDiseno;
}
