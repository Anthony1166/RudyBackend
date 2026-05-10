package portafolio.sami.rudy.servicesImp.proy;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.proy.CategoriaProyectoDTO;
import portafolio.sami.rudy.dto.proy.ProyectoDTO;
import portafolio.sami.rudy.entities.proy.ImagenProyecto;
import portafolio.sami.rudy.entities.proy.ProcesoProyecto;
import portafolio.sami.rudy.entities.proy.CategoriaProyecto;
import portafolio.sami.rudy.entities.proy.Proyecto;
import portafolio.sami.rudy.repositories.proy.CategoriaProyectoRepository;
import portafolio.sami.rudy.repositories.proy.ProyectoRepository;
import portafolio.sami.rudy.services.proy.ProyectoServices;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProyectoServicesImp implements ProyectoServices {

    private final ProyectoRepository proyectoRepository;
    private final ModelMapper modelMapper;
    private final CategoriaProyectoRepository categoriaRepository;
    private final S3Service s3Service;

    private String generarSlug(String titulo) {
        return titulo.toLowerCase()
                .replaceAll("[áäâà]", "a")
                .replaceAll("[éëêè]", "e")
                .replaceAll("[íïîì]", "i")
                .replaceAll("[óöôò]", "o")
                .replaceAll("[úüûù]", "u")
                .replaceAll("[ñ]", "n")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    // --- PÚBLICO ---

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoDTO> listarActivos() {
        return proyectoRepository.findAllActivosOrdenados().stream()
                .map(p -> modelMapper.map(p, ProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoDTO> listarPorAnio(Integer anio) {
        return proyectoRepository.findByAnioAndActivoTrue(anio).stream()
                .map(p -> modelMapper.map(p, ProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoDTO obtenerPorId(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con el ID: " + id));
        return modelMapper.map(proyecto, ProyectoDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ProyectoDTO obtenerPorSlug(String slug) {
        Proyecto proyecto = proyectoRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con el slug: " + slug));
        return modelMapper.map(proyecto, ProyectoDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoDTO> listarPorCategoria(Long categoriaId) {
        return proyectoRepository.findByCategorias_IdCategoriaAndActivoTrue(categoriaId).stream()
                .map(p -> modelMapper.map(p, ProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoDTO> buscarProyectos(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return listarActivos();
        }
        return proyectoRepository.buscarNivelDios(termino).stream()
                .map(p -> modelMapper.map(p, ProyectoDTO.class))
                .collect(Collectors.toList());
    }

    // --- ADMIN ---

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoDTO> listarTodosAdmin() {
        return proyectoRepository.findAllByOrderByOrdenAsc().stream()
                .map(p -> modelMapper.map(p, ProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProyectoDTO> listarPorCategoriaAdmin(Long categoriaId) {
        return proyectoRepository.findByCategoriaIdParaAdmin(categoriaId).stream()
                .map(p -> modelMapper.map(p, ProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProyectoDTO registrar(ProyectoDTO proyectoDTO) {
        Proyecto proyecto = modelMapper.map(proyectoDTO, Proyecto.class);

        proyecto.setSlug(generarSlug(proyectoDTO.getTitulo()));
        proyecto.setActivo(proyectoDTO.getActivo() != null ? proyectoDTO.getActivo() : true);

        if (proyectoDTO.getOrden() == null) {
            proyecto.setOrden(proyectoRepository.obtenerMaximoOrden() + 1);
        }

        if (proyectoDTO.getCategorias() != null && !proyectoDTO.getCategorias().isEmpty()) {
            List<CategoriaProyecto> categoriasReales = proyectoDTO.getCategorias().stream()
                    .map(cat -> categoriaRepository.findById(cat.getIdCategoria())
                            .orElseThrow(() -> new RuntimeException("Categoría no encontrada")))
                    .collect(Collectors.toList());
            proyecto.setCategorias(categoriasReales);
        } else {
            proyecto.setCategorias(new ArrayList<>());
        }

        Proyecto guardado = proyectoRepository.save(proyecto);
        return modelMapper.map(guardado, ProyectoDTO.class);
    }

    @Override
    @Transactional
    public ProyectoDTO actualizar(Long id, ProyectoDTO proyectoDTO) {
        Proyecto existente = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // 1. Rescate de datos originales
        Integer ordenOriginal = existente.getOrden();
        String slugOriginal = existente.getSlug();
        String tituloOriginal = existente.getTitulo();
        List<ImagenProyecto> imagenesOriginales = existente.getImagenes();
        List<ProcesoProyecto> procesosOriginales = existente.getProcesosDiseno();
        List<CategoriaProyectoDTO> categoriasNuevas = proyectoDTO.getCategorias();

        // 2. Barrera de hierro: ocultamos colecciones al ModelMapper
        proyectoDTO.setCategorias(null);
        proyectoDTO.setImagenes(null);
        proyectoDTO.setProcesosDiseno(null);

        modelMapper.map(proyectoDTO, existente);

        // 3. Restauramos lo que no debe cambiar por el mapper
        existente.setOrden(ordenOriginal);
        existente.setImagenes(imagenesOriginales);
        existente.setProcesosDiseno(procesosOriginales);

        // 4. Slug inteligente: solo regenera si el título cambió
        if (!tituloOriginal.equals(proyectoDTO.getTitulo())) {
            existente.setSlug(generarSlug(proyectoDTO.getTitulo()));
        } else {
            existente.setSlug(slugOriginal);
        }

        // 5. Categorías desde BD para evitar "detached entity"
        if (categoriasNuevas != null && !categoriasNuevas.isEmpty()) {
            List<CategoriaProyecto> categoriasReales = categoriasNuevas.stream()
                    .map(cat -> categoriaRepository.findById(cat.getIdCategoria())
                            .orElseThrow(() -> new RuntimeException("Categoría no encontrada")))
                    .collect(Collectors.toList());
            existente.setCategorias(categoriasReales);
        } else {
            existente.setCategorias(new ArrayList<>());
        }

        Proyecto guardado = proyectoRepository.save(existente);
        return modelMapper.map(guardado, ProyectoDTO.class);
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        proyecto.setActivo(!proyecto.getActivo());
        proyectoRepository.save(proyecto);
    }

    @Override
    @Transactional
    public void eliminarFisico(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        for (ImagenProyecto img : proyecto.getImagenes()) {
            borrarArchivoS3(img.getUrlImagen());
        }
        for (ProcesoProyecto proc : proyecto.getProcesosDiseno()) {
            borrarArchivoS3(proc.getUrlImagen());
        }

        proyectoRepository.delete(proyecto);
    }

    @Override
    @Transactional
    public void reordenarLote(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long proyectoId = idsOrdenados.get(i);
            int nuevoOrden = i + 1;
            proyectoRepository.findById(proyectoId).ifPresent(p -> {
                p.setOrden(nuevoOrden);
                proyectoRepository.save(p);
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
