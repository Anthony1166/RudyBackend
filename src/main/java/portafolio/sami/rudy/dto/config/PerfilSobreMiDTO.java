package portafolio.sami.rudy.dto.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilSobreMiDTO {
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;
    private String urlImagen;

    // Encuadre de la imagen
    private Double imgPosX;
    private Double imgPosY;
    private Double imgEscala;
    private Integer imgRotacion;
    private Boolean imgVolteoH;
    private Boolean imgVolteoV;

    // Bloque "Áreas de interés"
    private String subtituloAreas;
    private List<String> areas;

    @Pattern(regexp = "^(lista|texto)$", message = "areasModo debe ser 'lista' o 'texto'")
    private String areasModo;

    // Bloque "Idiomas"
    private String subtituloIdiomas;
    private List<String> idiomas;

    @Pattern(regexp = "^(lista|texto)$", message = "idiomasModo debe ser 'lista' o 'texto'")
    private String idiomasModo;
}
