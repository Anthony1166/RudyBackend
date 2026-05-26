package portafolio.sami.rudy.entities.prod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "procesos_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceso_id")
    private Long id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "url_imagen")
    private String urlImagen;

    // Cambiado: Antes no tenía @Column(name = "orden"), lo que asume nombre de columna "orden".
    // El error de la BD decía: "orden_proc_prod" of relation "procesos_producto" violates not-null constraint
    @Column(name = "orden_proc_prod", nullable = false)
    private Integer orden;

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