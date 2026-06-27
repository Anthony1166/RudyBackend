package portafolio.sami.rudy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import portafolio.sami.rudy.entities.config.PerfilSobreMi;
import portafolio.sami.rudy.repositories.config.PerfilSobreMiRepository;

/**
 * Siembra el registro único de la sección "Sobre mí" con el contenido actual
 * del home. Idempotente: solo crea la fila si la tabla está vacía, sin pisar
 * lo que el admin ya haya editado. Necesario porque ddl-auto=update no carga datos.
 */
@Component
@Order(2)
@RequiredArgsConstructor
public class PerfilSobreMiSeed implements CommandLineRunner {

    private final PerfilSobreMiRepository perfilRepository;

    @Override
    public void run(String... args) {
        if (perfilRepository.count() > 0) {
            return;
        }

        PerfilSobreMi p = new PerfilSobreMi();
        p.setTitulo("¡HOLA! SOY SAMI");
        p.setDescripcion("Estudiante de Diseño Industrial (9.º ciclo en la PUCP) enfocada en el desarrollo de productos funcionales y accesibles. Trabajo desde el prototipado y la manufactura, entendiendo el diseño como un proceso de prueba, error y ajuste continuo con una mirada centrada en el usuario.");
        p.setUrlImagen("/assets/estrellitaSami.png");

        // Encuadre por defecto: centro (0,0 = sin desplazamiento en modo cropper)
        p.setImgPosX(0.0);
        p.setImgPosY(0.0);
        p.setImgEscala(1.0);
        p.setImgRotacion(0);
        p.setImgVolteoH(false);
        p.setImgVolteoV(false);

        // Áreas de interés — se mostraba como texto separado por "|"
        p.setSubtituloAreas("ÁREAS DE INTERÉS");
        p.setAreas(String.join("\n",
                "Diseño de productos",
                "Manufactura en madera",
                "Manufactura en plásticos",
                "Educación infantil"));
        p.setAreasModo("texto");

        // Idiomas — se mostraba como lista
        p.setSubtituloIdiomas("IDIOMAS");
        p.setIdiomas(String.join("\n", "Inglés B2", "Francés A2"));
        p.setIdiomasModo("lista");

        perfilRepository.save(p);
    }
}
