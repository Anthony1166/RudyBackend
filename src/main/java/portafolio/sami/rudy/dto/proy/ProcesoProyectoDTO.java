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
public class ProcesoProyectoDTO {

    private Long id;

    @NotBlank(message = "El título de la fase es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private String urlImagen;

    @Min(value = 1, message = "El orden debe ser mayor a 0")
    private Integer orden;
}
