package portafolio.sami.rudy.servicesImp.proy;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.proy.ProcesoProyectoDTO;
import portafolio.sami.rudy.entities.proy.ProcesoProyecto;
import portafolio.sami.rudy.entities.proy.Proyecto;
import portafolio.sami.rudy.repositories.proy.ProcesoProyectoRepository;
import portafolio.sami.rudy.repositories.proy.ProyectoRepository;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.services.proy.ProcesoProyectoServices;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcesoProyectoServicesImp implements ProcesoProyectoServices {

    private final ProcesoProyectoRepository procesoRepository;
    private final ProyectoRepository proyectoRepository;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;

    @Override
    @Transactional(readOnly = true)
    public List<ProcesoProyectoDTO> listarPorProyecto(Long proyectoId) {
        return procesoRepository.findByProyectoIdProyectoOrderByOrdenAsc(proyectoId).stream()
                .map(p -> modelMapper.map(p, ProcesoProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProcesoProyectoDTO obtenerPorId(Long id) {
        ProcesoProyecto proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con el ID: " + id));
        return modelMapper.map(proceso, ProcesoProyectoDTO.class);
    }

    @Override
    @Transactional
    public ProcesoProyectoDTO agregarAProyecto(Long proyectoId, ProcesoProyectoDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        ProcesoProyecto proceso = modelMapper.map(dto, ProcesoProyecto.class);
        proceso.setProyecto(proyecto);

        if (proceso.getOrden() == null || proceso.getOrden() <= 0) {
            Integer maxOrden = procesoRepository.obtenerMaximoOrdenPorProyecto(proyectoId);
            proceso.setOrden(maxOrden != null ? maxOrden + 1 : 1);
        }

        return modelMapper.map(procesoRepository.save(proceso), ProcesoProyectoDTO.class);
    }

    @Override
    @Transactional
    public ProcesoProyectoDTO actualizar(Long id, ProcesoProyectoDTO dto) {
        ProcesoProyecto existente = procesoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con el ID: " + id));

        Long proyectoId = existente.getProyecto().getIdProyecto();

        if (dto.getUrlImagen() != null && !dto.getUrlImagen().equals(existente.getUrlImagen())) {
            borrarArchivoS3(existente.getUrlImagen());
        }

        existente.setTitulo(dto.getTitulo());
        existente.setDescripcion(dto.getDescripcion());
        existente.setUrlImagen(dto.getUrlImagen());

        // Ajustes de imagen (punto focal + zoom + rotar/voltear)
        existente.setPosX(dto.getPosX());
        existente.setPosY(dto.getPosY());
        existente.setEscala(dto.getEscala());
        existente.setRotacion(dto.getRotacion());
        existente.setVolteoH(dto.getVolteoH());
        existente.setVolteoV(dto.getVolteoV());

        Integer ordenAntiguo = existente.getOrden();
        Integer ordenNuevo = dto.getOrden();

        if (ordenNuevo != null && !ordenNuevo.equals(ordenAntiguo)) {
            if (ordenNuevo < ordenAntiguo) {
                List<ProcesoProyecto> afectados = procesoRepository
                        .findByProyectoIdProyectoAndOrdenBetween(proyectoId, ordenNuevo, ordenAntiguo - 1);
                afectados.forEach(p -> p.setOrden(p.getOrden() + 1));
                procesoRepository.saveAll(afectados);
            } else {
                List<ProcesoProyecto> afectados = procesoRepository
                        .findByProyectoIdProyectoAndOrdenBetween(proyectoId, ordenAntiguo + 1, ordenNuevo);
                afectados.forEach(p -> p.setOrden(p.getOrden() - 1));
                procesoRepository.saveAll(afectados);
            }
            existente.setOrden(ordenNuevo);
        }

        return modelMapper.map(procesoRepository.save(existente), ProcesoProyectoDTO.class);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ProcesoProyecto proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con el ID: " + id));
        borrarArchivoS3(proceso.getUrlImagen());
        procesoRepository.delete(proceso);
    }

    @Override
    @Transactional
    public void reordenarLote(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long procesoId = idsOrdenados.get(i);
            int nuevoOrden = i + 1;
            procesoRepository.findById(procesoId).ifPresent(p -> {
                p.setOrden(nuevoOrden);
                procesoRepository.save(p);
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
