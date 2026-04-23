package portafolio.sami.rudy.dto.prod;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a cero")
    private Double precio;

    // Estos pueden ser opcionales, así que no les ponemos validación estricta
    private String caracteristicas;
    private String subTitulo;

    // 🔥 LOS NUEVOS CAMPOS
    private String slug;
    private Boolean destacado;

    // Le quitamos el @NotNull para que el backend pueda hacer su magia
    @Min(value = 0, message = "El orden no puede ser negativo")
    private Integer orden;


    // Los superpoderes de administración:
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Boolean activo;

    // Las relaciones, pero ahora apuntando a los DTOs, no a las Entidades:
    private List<CategoriaProductoDTO> categorias;
    private List<ImagenProductoDTO> imagenes;
    private List<ProcesoProductoDTO> procesos;
}
