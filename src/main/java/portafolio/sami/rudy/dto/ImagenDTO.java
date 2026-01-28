package portafolio.sami.rudy.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import portafolio.sami.rudy.entities.Proyecto;
@Getter
@Setter
public class ImagenDTO {
    private Long id;
    private String imagen;
    private String descripcion;
}
