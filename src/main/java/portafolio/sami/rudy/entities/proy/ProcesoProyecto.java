package portafolio.sami.rudy.entities.proy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proceso_diseno", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"proyecto_id", "orden"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcesoProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo_fase", nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "imagen_proceso")
    private String urlImagen;

    @Column(nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    @JsonIgnore
    private Proyecto proyecto;
}
