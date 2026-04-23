package portafolio.sami.rudy.entities;

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
public class ProcesoDiseno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo_fase;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    private String imagen_proceso;

    @Column(nullable = false)
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    @JsonIgnore
    private Proyecto proyecto;
}
