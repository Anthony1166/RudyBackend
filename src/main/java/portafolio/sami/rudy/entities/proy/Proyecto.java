package portafolio.sami.rudy.entities.proy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProyecto;

    private String titulo;
    private String subtitulo;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private Integer anio;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "orden")
    private Integer orden = 0;

    @ManyToMany
    @JoinTable(
        name = "proyecto_categoria",
        joinColumns = @JoinColumn(name = "proyecto_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<CategoriaProyecto> categorias = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ImagenProyecto> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcesoProyecto> procesosDiseno = new ArrayList<>();
}
