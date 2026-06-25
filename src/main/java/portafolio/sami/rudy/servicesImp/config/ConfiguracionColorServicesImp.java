package portafolio.sami.rudy.servicesImp.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.config.ConfiguracionColorDTO;
import portafolio.sami.rudy.entities.config.ConfiguracionColor;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.config.ConfiguracionColorRepository;
import portafolio.sami.rudy.services.config.ConfiguracionColorServices;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfiguracionColorServicesImp implements ConfiguracionColorServices {

    private final ConfiguracionColorRepository configRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConfiguracionColorDTO> listarTodas() {
        return configRepository.findAllByOrderByIdAsc().stream()
                .map(c -> modelMapper.map(c, ConfiguracionColorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConfiguracionColorDTO actualizar(String clave, ConfiguracionColorDTO dto) {
        ConfiguracionColor existente = configRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración de color no encontrada: " + clave));

        // Solo se modifican los dos colores. La clave, el nombre y los defaults son intocables.
        if (dto.getColorTop() != null) {
            existente.setColorTop(dto.getColorTop());
        }
        if (dto.getColorBottom() != null) {
            existente.setColorBottom(dto.getColorBottom());
        }

        ConfiguracionColor actualizada = configRepository.save(existente);
        return modelMapper.map(actualizada, ConfiguracionColorDTO.class);
    }

    @Override
    @Transactional
    public ConfiguracionColorDTO restaurarDefault(String clave) {
        ConfiguracionColor existente = configRepository.findByClave(clave)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración de color no encontrada: " + clave));

        // Devolvemos los colores a sus valores originales sembrados
        existente.setColorTop(existente.getColorTopDefault());
        existente.setColorBottom(existente.getColorBottomDefault());

        ConfiguracionColor restaurada = configRepository.save(existente);
        return modelMapper.map(restaurada, ConfiguracionColorDTO.class);
    }
}
