package portafolio.sami.rudy.servicesImp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.config.PerfilSobreMiDTO;
import portafolio.sami.rudy.entities.config.PerfilSobreMi;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.config.PerfilSobreMiRepository;
import portafolio.sami.rudy.services.config.PerfilSobreMiServices;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfilSobreMiServicesImp implements PerfilSobreMiServices {

    private final PerfilSobreMiRepository perfilRepository;

    @Override
    @Transactional(readOnly = true)
    public PerfilSobreMiDTO obtener() {
        PerfilSobreMi perfil = perfilRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Perfil 'Sobre mí' no encontrado"));
        return aDTO(perfil);
    }

    @Override
    @Transactional
    public PerfilSobreMiDTO actualizar(PerfilSobreMiDTO dto) {
        PerfilSobreMi perfil = perfilRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Perfil 'Sobre mí' no encontrado"));

        perfil.setTitulo(dto.getTitulo());
        perfil.setDescripcion(dto.getDescripcion());

        if (dto.getUrlImagen() != null) {
            perfil.setUrlImagen(dto.getUrlImagen());
        }

        // Encuadre de imagen
        perfil.setImgPosX(dto.getImgPosX());
        perfil.setImgPosY(dto.getImgPosY());
        perfil.setImgEscala(dto.getImgEscala());
        perfil.setImgRotacion(dto.getImgRotacion());
        perfil.setImgVolteoH(dto.getImgVolteoH());
        perfil.setImgVolteoV(dto.getImgVolteoV());

        // Bloques de listas
        perfil.setSubtituloAreas(dto.getSubtituloAreas());
        perfil.setAreas(unir(dto.getAreas()));
        if (dto.getAreasModo() != null) {
            perfil.setAreasModo(dto.getAreasModo());
        }

        perfil.setSubtituloIdiomas(dto.getSubtituloIdiomas());
        perfil.setIdiomas(unir(dto.getIdiomas()));
        if (dto.getIdiomasModo() != null) {
            perfil.setIdiomasModo(dto.getIdiomasModo());
        }

        PerfilSobreMi actualizado = perfilRepository.save(perfil);
        return aDTO(actualizado);
    }

    // ── Mapeo manual (las listas se guardan como texto separado por \n) ──

    private PerfilSobreMiDTO aDTO(PerfilSobreMi p) {
        PerfilSobreMiDTO dto = new PerfilSobreMiDTO();
        dto.setId(p.getId());
        dto.setTitulo(p.getTitulo());
        dto.setDescripcion(p.getDescripcion());
        dto.setUrlImagen(p.getUrlImagen());
        dto.setImgPosX(p.getImgPosX());
        dto.setImgPosY(p.getImgPosY());
        dto.setImgEscala(p.getImgEscala());
        dto.setImgRotacion(p.getImgRotacion());
        dto.setImgVolteoH(p.getImgVolteoH());
        dto.setImgVolteoV(p.getImgVolteoV());
        dto.setSubtituloAreas(p.getSubtituloAreas());
        dto.setAreas(separar(p.getAreas()));
        dto.setAreasModo(p.getAreasModo());
        dto.setSubtituloIdiomas(p.getSubtituloIdiomas());
        dto.setIdiomas(separar(p.getIdiomas()));
        dto.setIdiomasModo(p.getIdiomasModo());
        return dto;
    }

    private List<String> separar(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return Arrays.stream(texto.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String unir(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return items.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("\n"));
    }
}
