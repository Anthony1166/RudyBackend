package portafolio.sami.rudy.controllers.config;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.config.PerfilSobreMiDTO;
import portafolio.sami.rudy.services.config.PerfilSobreMiServices;

@RestController
@RequestMapping("/sami")
public class PerfilSobreMiController {

    @Autowired
    private PerfilSobreMiServices perfilServices;

    // --- PÚBLICO (el home carga la sección "Sobre mí") ---

    @GetMapping("/perfil-sobre-mi")
    public PerfilSobreMiDTO obtener() {
        return perfilServices.obtener();
    }

    // --- PRIVADO (Panel de Control) ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/perfil-sobre-mi")
    public PerfilSobreMiDTO actualizar(@Valid @RequestBody PerfilSobreMiDTO dto) {
        return perfilServices.actualizar(dto);
    }
}
