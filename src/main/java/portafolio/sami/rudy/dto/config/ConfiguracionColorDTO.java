package portafolio.sami.rudy.dto.config;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionColorDTO {
    private Long id;
    private String clave;
    private String nombre;

    @Pattern(regexp = "^#([0-9a-fA-F]{6})$", message = "colorTop debe ser un hex válido (ej: #2495FF)")
    private String colorTop;

    @Pattern(regexp = "^#([0-9a-fA-F]{6})$", message = "colorBottom debe ser un hex válido (ej: #B7CF49)")
    private String colorBottom;

    // Solo lectura: se exponen para que el panel pueda ofrecer "Restaurar default"
    private String colorTopDefault;
    private String colorBottomDefault;
}
