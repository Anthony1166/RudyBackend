package portafolio.sami.rudy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;
    
    private String nombre;

    // Relación inversa: Una categoría está en muchos proyectos
    @ManyToMany(mappedBy = "categorias")
    @JsonIgnore // Importante para evitar bucle infinito al serializar Proyecto -> Categorias -> Proyectos...
    private List<Proyecto> proyectos;
}
