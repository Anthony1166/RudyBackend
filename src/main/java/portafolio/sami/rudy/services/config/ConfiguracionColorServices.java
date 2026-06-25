package portafolio.sami.rudy.services.config;

import portafolio.sami.rudy.dto.config.ConfiguracionColorDTO;

import java.util.List;

public interface ConfiguracionColorServices {
    List<ConfiguracionColorDTO> listarTodas();
    ConfiguracionColorDTO actualizar(String clave, ConfiguracionColorDTO dto);
    ConfiguracionColorDTO restaurarDefault(String clave);
}
