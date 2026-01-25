package portafolio.sami.rudy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProyecto;
    
    private String titulo;
    private String descripcion;
    private Integer anio;

    // Relación Muchos a Muchos: Un proyecto tiene muchas categorías
    @ManyToMany
    @JoinTable(
        name = "proyecto_categoria", // Nombre de la tabla intermedia en BD
        joinColumns = @JoinColumn(name = "proyecto_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    // Quitamos @JsonIgnore aquí temporalmente. 
    // Lo ideal será manejar esto con DTOs para evitar bucles, 
    // pero en la entidad la relación debe existir.
    private List<Categoria> categorias = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Imagen> imagenes;
}
