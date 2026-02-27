package portafolio.sami.rudy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcesoDisenoDTO {

    private Long id;
    private String titulo_fase;
    private String descripcion;
    private String imagen_proceso;
    private Integer orden;
    
    // No incluimos la referencia al ProyectoDTO para evitar redundancia,
    // ya que este DTO se usará principalmente dentro de la lista de un ProyectoDTO.
    // private ProyectoDTO proyecto;
}
