package portafolio.sami.rudy.servicesImp.proy;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.proy.CategoriaProyectoDTO;
import portafolio.sami.rudy.entities.proy.CategoriaProyecto;
import portafolio.sami.rudy.repositories.proy.CategoriaProyectoRepository;
import portafolio.sami.rudy.services.proy.CategoriaProyectoServices;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaProyectoServicesImp implements CategoriaProyectoServices {

    private final CategoriaProyectoRepository categoriaRepository;
    private final ModelMapper modelMapper;

    private String generarSlug(String nombre) {
        return nombre.toLowerCase()
                .replaceAll("[áäâà]", "a")
                .replaceAll("[éëêè]", "e")
                .replaceAll("[íïîì]", "i")
                .replaceAll("[óöôò]", "o")
                .replaceAll("[úüûù]", "u")
                .replaceAll("[ñ]", "n")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProyectoDTO> listarTodas() {
        return categoriaRepository.findAllByOrderByOrdenAsc().stream()
                .map(c -> modelMapper.map(c, CategoriaProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProyectoDTO> listarPublicas() {
        return categoriaRepository.buscarCategoriasConProyectosActivos().stream()
                .map(c -> modelMapper.map(c, CategoriaProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProyectoDTO obtenerPorId(Long id) {
        CategoriaProyecto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return modelMapper.map(categoria, CategoriaProyectoDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProyectoDTO> buscarCategorias(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return listarTodas();
        }
        return categoriaRepository.buscarCategoriaNivelDios(termino).stream()
                .map(c -> modelMapper.map(c, CategoriaProyectoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoriaProyectoDTO guardar(CategoriaProyectoDTO categoriaDTO) {
        String nombreLimpio = categoriaDTO.getNombre().trim();
        categoriaDTO.setNombre(nombreLimpio);

        String slugPropuesto = generarSlug(nombreLimpio);

        boolean nombreOcupado = categoriaRepository.existsByNombre(nombreLimpio);
        boolean slugOcupado = categoriaRepository.findBySlug(slugPropuesto).isPresent();

        if (nombreOcupado || slugOcupado) {
            throw new RuntimeException("Ya tienes una categoría con este nombre. Por favor, elige otro.");
        }

        CategoriaProyecto categoria = modelMapper.map(categoriaDTO, CategoriaProyecto.class);
        categoria.setSlug(slugPropuesto);
        categoria.setActivo(true);

        if (categoriaDTO.getOrden() == null) {
            categoria.setOrden(categoriaRepository.obtenerMaximoOrden() + 1);
        }

        CategoriaProyecto guardada = categoriaRepository.save(categoria);
        return modelMapper.map(guardada, CategoriaProyectoDTO.class);
    }

    @Override
    @Transactional
    public CategoriaProyectoDTO actualizar(Long id, CategoriaProyectoDTO categoriaDTO) {
        CategoriaProyecto existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!existente.getNombre().equalsIgnoreCase(categoriaDTO.getNombre())) {
            String nuevoSlug = generarSlug(categoriaDTO.getNombre());

            boolean nombreOcupado = categoriaRepository.existsByNombre(categoriaDTO.getNombre());
            boolean slugOcupado = categoriaRepository.findBySlug(nuevoSlug)
                    .filter(cat -> !cat.getIdCategoria().equals(id))
                    .isPresent();

            if (nombreOcupado || slugOcupado) {
                throw new RuntimeException("Ya tienes otra categoría con este nombre o URL. Por favor, elige otro.");
            }

            existente.setSlug(nuevoSlug);
        }

        existente.setNombre(categoriaDTO.getNombre());

        if (categoriaDTO.getUrlImagen() != null) {
            existente.setUrlImagen(categoriaDTO.getUrlImagen());
        }

        if (categoriaDTO.getActivo() != null) {
            existente.setActivo(categoriaDTO.getActivo());
        }

        CategoriaProyecto actualizada = categoriaRepository.save(existente);
        return modelMapper.map(actualizada, CategoriaProyectoDTO.class);
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        CategoriaProyecto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void eliminarFisico(Long id) {
        CategoriaProyecto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (categoria.getProyectos() != null) {
            categoria.getProyectos().forEach(proyecto -> {
                proyecto.getCategorias().remove(categoria);
            });
        }

        categoriaRepository.delete(categoria);
    }

    @Override
    @Transactional
    public void reordenarLote(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long id = idsOrdenados.get(i);
            int nuevoOrden = i + 1;
            categoriaRepository.findById(id).ifPresent(c -> {
                c.setOrden(nuevoOrden);
                categoriaRepository.save(c);
            });
        }
    }
}
