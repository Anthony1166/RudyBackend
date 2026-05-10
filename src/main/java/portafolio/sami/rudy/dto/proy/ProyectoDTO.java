package portafolio.sami.rudy.dto.proy;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "El título del proyecto es obligatorio")
    private String titulo;

    private String subtitulo;

    private String descripcion;

    @NotNull(message = "El año del proyecto es obligatorio")
    @Min(value = 1900, message = "El año no puede ser anterior a 1900")
    @Max(value = 2099, message = "El año no puede ser posterior a 2099")
    private Integer anio;

    private String slug;
    private Boolean activo;

    @Min(value = 0, message = "El orden no puede ser negativo")
    private Integer orden;

    private List<CategoriaProyectoDTO> categorias;
    private List<ImagenProyectoDTO> imagenes;
    private List<ProcesoProyectoDTO> procesosDiseno;
}
