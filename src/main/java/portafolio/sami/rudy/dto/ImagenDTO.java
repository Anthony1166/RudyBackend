package portafolio.sami.rudy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenDTO {
    private Long id;
    private String imagen;
    private String descripcion;
}
