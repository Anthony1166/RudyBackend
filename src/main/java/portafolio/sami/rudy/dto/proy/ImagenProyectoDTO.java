package portafolio.sami.rudy.dto.proy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenProyectoDTO {

    private Long id;

    @NotBlank(message = "La URL de la imagen no puede estar vacía")
    private String urlImagen;

    private String textoAlternativo;

    @Min(value = 0, message = "El orden no puede ser negativo")
    private Integer orden;

    private Boolean esPortada;
}
