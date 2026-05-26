package portafolio.sami.rudy.entities.prod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagenes_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imagen_id")
    private Long id;

    @Column(name = "url_imagen", nullable = false)
    private String urlImagen;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "es_portada")
    private Boolean esPortada;

    @Column(name = "texto_alternativo")
    private String textoAlternativo;

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
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
