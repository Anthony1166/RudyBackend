package portafolio.sami.rudy.dto.prod;

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
public class ProcesoProductoDTO {
    private Long id;

    private String titulo; // Opcional, por si solo quiere poner una foto con texto

    @NotBlank(message = "La descripción del proceso es obligatoria")
    private String descripcion;

    private String urlImagen; // Opcional, un paso del proceso podría ser solo texto

    @Min(value = 0, message = "El orden no puede ser negativo")
    private Integer orden;
}
