package portafolio.sami.rudy.entities.config;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "perfil_sobre_mi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilSobreMi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perfil_id")
    private Long id;

    @Column(name = "titulo", columnDefinition = "TEXT")
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "url_imagen", columnDefinition = "TEXT")
    private String urlImagen;

    // Encuadre de la imagen (punto focal + zoom + rotar/voltear)
    @Column(name = "img_pos_x")
    private Double imgPosX;

    @Column(name = "img_pos_y")
    private Double imgPosY;

    @Column(name = "img_escala")
    private Double imgEscala;

    @Column(name = "img_rotacion")
    private Integer imgRotacion;

    @Column(name = "img_volteo_h")
    private Boolean imgVolteoH;

    @Column(name = "img_volteo_v")
    private Boolean imgVolteoV;

    // Bloque "Áreas de interés"
    @Column(name = "subtitulo_areas", columnDefinition = "TEXT")
    private String subtituloAreas;

    // Ítems separados por salto de línea (\n)
    @Column(name = "areas", columnDefinition = "TEXT")
    private String areas;

    // Modo de visualización: "lista" o "texto"
    @Column(name = "areas_modo")
    private String areasModo;

    // Bloque "Idiomas"
    @Column(name = "subtitulo_idiomas", columnDefinition = "TEXT")
    private String subtituloIdiomas;

    @Column(name = "idiomas", columnDefinition = "TEXT")
    private String idiomas;

    @Column(name = "idiomas_modo")
    private String idiomasModo;
}
