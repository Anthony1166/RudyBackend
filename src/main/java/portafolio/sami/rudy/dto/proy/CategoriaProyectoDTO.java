package portafolio.sami.rudy.dto.proy;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProyectoDTO {

    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    private String urlImagen;
    private String slug;
    private Boolean activo;

    @Min(value = 0, message = "El orden no puede ser negativo")
    private Integer orden;

    @DecimalMin(value = "0.0", message = "posX debe ser >= 0")
    @DecimalMax(value = "100.0", message = "posX debe ser <= 100")
    private Double posX;

    @DecimalMin(value = "0.0", message = "posY debe ser >= 0")
    @DecimalMax(value = "100.0", message = "posY debe ser <= 100")
    private Double posY;

    @DecimalMin(value = "1.0", message = "escala debe ser >= 1")
    @DecimalMax(value = "3.0", message = "escala debe ser <= 3")
    private Double escala;

    @Min(value = 0, message = "rotacion debe ser >= 0")
    @Max(value = 270, message = "rotacion debe ser <= 270")
    private Integer rotacion;

    private Boolean volteoH;
    private Boolean volteoV;
}
