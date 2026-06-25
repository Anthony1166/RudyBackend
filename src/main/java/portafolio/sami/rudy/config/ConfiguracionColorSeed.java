package portafolio.sami.rudy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import portafolio.sami.rudy.entities.config.ConfiguracionColor;
import portafolio.sami.rudy.repositories.config.ConfiguracionColorRepository;

import java.util.List;

/**
 * Siembra las 6 configuraciones de color de fondo de las secciones públicas.
 * Es idempotente: solo crea las claves que falten, sin pisar los valores que
 * el admin ya haya personalizado. Necesario porque ddl-auto=update no carga datos.
 */
@Component
@RequiredArgsConstructor
public class ConfiguracionColorSeed implements CommandLineRunner {

    private final ConfiguracionColorRepository configRepository;

    private record Semilla(String clave, String nombre, String top, String bottom) {}

    private static final List<Semilla> DEFAULTS = List.of(
            new Semilla("home",                "Inicio",                  "#000000", "#B7CF49"),
            new Semilla("tienda-categorias",   "Tienda · Categorías",     "#2495FF", "#B7CF49"),
            new Semilla("tienda-productos",    "Tienda · Productos",      "#2495FF", "#B7CF49"),
            new Semilla("producto-detalle",    "Tienda · Detalle producto","#2495FF", "#B7CF49"),
            new Semilla("portafolio-galeria",  "Portafolio · Galería",    "#2495FF", "#B7CF49"),
            new Semilla("portafolio-detalle",  "Portafolio · Detalle",    "#2495FF", "#B7CF49"),
            new Semilla("perfil-home",         "Perfil · Inicio",         "#B7CF49", "#2495FF"),
            new Semilla("perfil-pagina",       "Perfil · Página",         "#155999", "#2495FF")
    );

    @Override
    public void run(String... args) {
        for (Semilla s : DEFAULTS) {
            if (!configRepository.existsByClave(s.clave())) {
                ConfiguracionColor c = new ConfiguracionColor();
                c.setClave(s.clave());
                c.setNombre(s.nombre());
                c.setColorTop(s.top());
                c.setColorBottom(s.bottom());
                c.setColorTopDefault(s.top());
                c.setColorBottomDefault(s.bottom());
                configRepository.save(c);
            }
        }
    }
}
