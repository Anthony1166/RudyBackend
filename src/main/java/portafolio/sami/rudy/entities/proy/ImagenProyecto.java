package portafolio.sami.rudy.entities.proy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imagen", nullable = false)
    private String urlImagen;

    @Column(name = "texto_alternativo")
    private String textoAlternativo;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "es_portada")
    private Boolean esPortada;

    @Column(name = "pos_x")
    private Double posX;

    @Column(name = "pos_y")
    private Double posY;

    @Column(name = "escala")
    private Double escala;

    @Column(name = "rotacion")
    private Integer rotacion;

    @Column(name = "volteo_h")
    private Boolean volteoH;

    @Column(name = "volteo_v")
    private Boolean volteoV;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;
}
