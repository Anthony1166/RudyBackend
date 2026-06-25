package portafolio.sami.rudy.entities.config;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuracion_colores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "configuracion_id")
    private Long id;

    // Identifica el componente (ej: home, tienda-categorias, producto-detalle)
    @Column(name = "clave", nullable = false, unique = true)
    private String clave;

    // Etiqueta legible para el panel de admin (ej: "Inicio", "Tienda · Categorías")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    // Colores actuales del degradado vertical
    @Column(name = "color_top", nullable = false)
    private String colorTop;

    @Column(name = "color_bottom", nullable = false)
    private String colorBottom;

    // Colores originales (inmutables) — sirven para "Restaurar default"
    @Column(name = "color_top_default", nullable = false)
    private String colorTopDefault;

    @Column(name = "color_bottom_default", nullable = false)
    private String colorBottomDefault;
}
