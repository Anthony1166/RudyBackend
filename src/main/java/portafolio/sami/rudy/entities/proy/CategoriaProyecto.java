package portafolio.sami.rudy.entities.proy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "url_imagen")
    private String urlImagen;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "orden")
    private Integer orden = 0;

    @JsonIgnore
    @ManyToMany(mappedBy = "categorias")
    private List<Proyecto> proyectos;
}
