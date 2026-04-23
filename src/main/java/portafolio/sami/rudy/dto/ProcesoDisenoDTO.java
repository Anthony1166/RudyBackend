package portafolio.sami.rudy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoDisenoDTO {

    private Long id;
    private String titulo_fase;
    private String descripcion;
    private String imagen_proceso;
    private Integer orden;

}
