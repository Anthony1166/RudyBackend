package portafolio.sami.rudy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "proceso_diseno", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"proyecto_id", "orden"})
})
@Getter
@Setter
public class ProcesoDiseno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo_fase;

    // SOLUCIÓN: Reemplazar @Lob por @Column(columnDefinition = "TEXT")
    // Esto le dice a Hibernate que use el tipo de dato TEXT de PostgreSQL,
    // que no tiene la restricción del modo auto-commit.
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    private String imagen_proceso; // URL de la imagen de Cloudflare

    @Column(nullable = false)
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyecto_id", nullable = false)
    @JsonIgnore // Esencial para evitar bucles infinitos al serializar
    private Proyecto proyecto;
}
