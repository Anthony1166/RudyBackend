package portafolio.sami.rudy.servicesImp.proy;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import portafolio.sami.rudy.dto.proy.ImagenProyectoDTO;
import portafolio.sami.rudy.entities.proy.ImagenProyecto;
import portafolio.sami.rudy.entities.proy.Proyecto;
import portafolio.sami.rudy.repositories.proy.ImagenProyectoRepository;
import portafolio.sami.rudy.repositories.proy.ProyectoRepository;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.services.proy.ImagenProyectoServices;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImagenProyectoServicesImp implements ImagenProyectoServices {

    private final ImagenProyectoRepository imagenRepository;
    private final ProyectoRepository proyectoRepository;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;

    // Cuando una imagen quiere ser portada, destittuye la anterior automáticamente
    private void gestionarPortada(Long proyectoId, Boolean nuevaEsPortada) {
        if (nuevaEsPortada != null && nuevaEsPortada) {
            imagenRepository.findByProyectoIdProyectoAndEsPortadaTrue(proyectoId).ifPresent(antigua -> {
                antigua.setEsPortada(false);
                imagenRepository.save(antigua);
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImagenProyectoDTO> listarPorProyecto(Long proyectoId) {
        return imagenRepository.findByProyectoIdProyectoOrderByOrdenAsc(proyectoId).stream()
                .map(i -> modelMapper.map(i, ImagenProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ImagenProyectoDTO obtenerPorId(Long id) {
        ImagenProyecto imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada"));
        return modelMapper.map(imagen, ImagenProyectoDTO.class);
    }

    @Override
    @Transactional
    public ImagenProyectoDTO agregarAProyecto(Long proyectoId, ImagenProyectoDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        ImagenProyecto imagen = modelMapper.map(dto, ImagenProyecto.class);
        imagen.setProyecto(proyecto);

        if (dto.getEsPortada() == null) imagen.setEsPortada(false);
        gestionarPortada(proyectoId, imagen.getEsPortada());

        if (dto.getOrden() == null) {
            imagen.setOrden(imagenRepository.obtenerMaximoOrdenPorProyecto(proyectoId) + 1);
        }

        ImagenProyecto guardada = imagenRepository.save(imagen);
        return modelMapper.map(guardada, ImagenProyectoDTO.class);
    }

    @Override
    @Transactional
    public ImagenProyectoDTO actualizar(Long id, ImagenProyectoDTO dto) {
        ImagenProyecto existente = imagenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada"));

        Long proyectoId = existente.getProyecto().getIdProyecto();

        existente.setUrlImagen(dto.getUrlImagen());
        existente.setTextoAlternativo(dto.getTextoAlternativo());

        // Gestión de portada
        if (dto.getEsPortada() != null && !dto.getEsPortada().equals(existente.getEsPortada())) {
            gestionarPortada(proyectoId, dto.getEsPortada());
            existente.setEsPortada(dto.getEsPortada());
        }

        // Magia del Empuje (shift) al cambiar orden
        Integer ordenAntiguo = existente.getOrden();
        Integer ordenNuevo = dto.getOrden();

        if (ordenNuevo != null && !ordenNuevo.equals(ordenAntiguo)) {
            if (ordenNuevo < ordenAntiguo) {
                List<ImagenProyecto> afectadas = imagenRepository
                        .findByProyectoIdProyectoAndOrdenBetween(proyectoId, ordenNuevo, ordenAntiguo - 1);
                afectadas.forEach(i -> i.setOrden(i.getOrden() + 1));
                imagenRepository.saveAll(afectadas);
            } else {
                List<ImagenProyecto> afectadas = imagenRepository
                        .findByProyectoIdProyectoAndOrdenBetween(proyectoId, ordenAntiguo + 1, ordenNuevo);
                afectadas.forEach(i -> i.setOrden(i.getOrden() - 1));
                imagenRepository.saveAll(afectadas);
            }
            existente.setOrden(ordenNuevo);
        }

        ImagenProyecto actualizada = imagenRepository.save(existente);
        return modelMapper.map(actualizada, ImagenProyectoDTO.class);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ImagenProyecto imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada"));
        borrarArchivoS3(imagen.getUrlImagen());
        imagenRepository.delete(imagen);
    }

    @Override
    @Transactional
    public void reordenarLote(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long imagenId = idsOrdenados.get(i);
            int nuevoOrden = i + 1;
            imagenRepository.findById(imagenId).ifPresent(img -> {
                img.setOrden(nuevoOrden);
                imagenRepository.save(img);
            });
        }
    }

    private void borrarArchivoS3(String url) {
        if (url == null || url.isBlank()) return;
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            s3Service.deleteFile(fileName);
        } catch (Exception ignored) {
        }
    }
}
