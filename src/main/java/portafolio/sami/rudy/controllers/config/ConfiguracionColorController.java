package portafolio.sami.rudy.controllers.config;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.config.ConfiguracionColorDTO;
import portafolio.sami.rudy.services.config.ConfiguracionColorServices;

import java.util.List;

@RestController
@RequestMapping("/sami")
public class ConfiguracionColorController {

    @Autowired
    private ConfiguracionColorServices configServices;

    // --- PÚBLICO (el sitio carga los colores al iniciar) ---

    @GetMapping("/configuracion-colores")
    public List<ConfiguracionColorDTO> listarTodas() {
        return configServices.listarTodas();
    }

    // --- PRIVADO (Panel de Control) ---

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/configuracion-colores/{clave}")
    public ConfiguracionColorDTO actualizar(@PathVariable String clave,
                                            @Valid @RequestBody ConfiguracionColorDTO dto) {
        return configServices.actualizar(clave, dto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/configuracion-colores/{clave}/restaurar")
    public ConfiguracionColorDTO restaurarDefault(@PathVariable String clave) {
        return configServices.restaurarDefault(clave);
    }
}
