package portafolio.sami.rudy.entities.prod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categorias_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoria_id")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "url_imagen")
    private String urlImagen;

    // 🔥 LOS NUEVOS CAMPOS (Caprichos y Orden)
    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "orden")
    private Integer orden = 0;

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
    @ManyToMany(mappedBy = "categorias")
    private List<Producto> productos;
}
