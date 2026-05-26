package portafolio.sami.rudy.servicesImp.prod;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.prod.CategoriaProductoDTO;
import portafolio.sami.rudy.entities.prod.CategoriaProducto;
import portafolio.sami.rudy.repositories.prod.CategoriaProductoRepository;
import portafolio.sami.rudy.exceptions.BusinessException;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.services.prod.CategoriaProductoServices;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaProductoServicesImp implements CategoriaProductoServices {

    private final CategoriaProductoRepository categoriaRepository;
    private final ModelMapper modelMapper;

    // 🔥 HERRAMIENTA NINJA: Limpia el texto para crear la URL (el Slug)
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
    public List<CategoriaProductoDTO> listarTodas() {
        // Ahora usamos el metodo que las trae ordenadas
        return categoriaRepository.findAllByOrderByOrdenAsc().stream()
                .map(c -> modelMapper.map(c, CategoriaProductoDTO.class))
                .collect(Collectors.toList());
    }

    // EL NUEVO MeTODO PARA EL MENÚ DE LA TIENDA
    @Transactional(readOnly = true)
    public List<CategoriaProductoDTO> listarPublicas() {
        return categoriaRepository.buscarCategoriasConProductosActivos().stream()
                .map(c -> modelMapper.map(c, CategoriaProductoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProductoDTO obtenerPorId(Long id) {
        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return modelMapper.map(categoria, CategoriaProductoDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProductoDTO> buscarCategorias(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return listarTodas();
        }
        // Llamamos al metodo "Nivel Dios" que pusiste en el Repositorio
        return categoriaRepository.buscarCategoriaNivelDios(termino).stream()
                .map(c -> modelMapper.map(c, CategoriaProductoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoriaProductoDTO guardar(CategoriaProductoDTO categoriaDTO) {
        // 1. Limpieza ninja: Quitamos espacios accidentales al inicio y final
        String nombreLimpio = categoriaDTO.getNombre().trim();
        categoriaDTO.setNombre(nombreLimpio); // Guardamos el nombre ya limpio

        // Generamos el slug en base al nombre limpio
        String slugPropuesto = generarSlug(nombreLimpio);

        // 2. Revisamos si el nombre exacto o la URL ya están ocupados
        boolean nombreOcupado = categoriaRepository.existsByNombre(nombreLimpio);
        boolean slugOcupado = categoriaRepository.findBySlug(slugPropuesto).isPresent();

        if (nombreOcupado || slugOcupado) {
            // 🔥 ESTO ES LO QUE DISPARA EL CUADRO ROJO EN ANGULAR
            throw new BusinessException("Ya tienes una categoría con este nombre. Por favor, elige otro.");
        }

        // 3. Si todo está libre, procedemos a guardar normalmente
        CategoriaProducto categoria = modelMapper.map(categoriaDTO, CategoriaProducto.class);
        categoria.setSlug(slugPropuesto);
        categoria.setActivo(true);

        if (categoriaDTO.getOrden() == null) {
            categoria.setOrden(categoriaRepository.obtenerMaximoOrden() + 1);
        }

        CategoriaProducto guardada = categoriaRepository.save(categoria);
        return modelMapper.map(guardada, CategoriaProductoDTO.class);
    }

    @Override
    @Transactional
    public CategoriaProductoDTO actualizar(Long id, CategoriaProductoDTO categoriaDTO) {
        CategoriaProducto existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // Si el nombre cambió, validamos a fondo
        if (!existente.getNombre().equalsIgnoreCase(categoriaDTO.getNombre())) {
            String nuevoSlug = generarSlug(categoriaDTO.getNombre());

            // Verificamos si el nombre o el nuevo slug ya le pertenecen a OTRA categoría distinta
            boolean nombreOcupado = categoriaRepository.existsByNombre(categoriaDTO.getNombre());
            boolean slugOcupado = categoriaRepository.findBySlug(nuevoSlug)
                    .filter(cat -> !cat.getId().equals(id)) // Ignoramos si es ella misma
                    .isPresent();

            if (nombreOcupado || slugOcupado) {
                // Usamos la propiedad "mensaje" para que Angular atrape exactamente este texto
                throw new BusinessException("Ya tienes otra categoría con este nombre o URL. Por favor, elige otro.");
            }

            // Si todo está libre, actualizamos el slug
            existente.setSlug(nuevoSlug);
        }

        // 3. ACTUALIZACIÓN QUIRÚRGICA: Solo tocamos lo que viene del Modal
        existente.setNombre(categoriaDTO.getNombre());

        if (categoriaDTO.getUrlImagen() != null) {
            existente.setUrlImagen(categoriaDTO.getUrlImagen());
        }

        if (categoriaDTO.getActivo() != null) {
            existente.setActivo(categoriaDTO.getActivo());
        }

        // Ajustes de imagen (punto focal + zoom + rotar/voltear)
        existente.setPosX(categoriaDTO.getPosX());
        existente.setPosY(categoriaDTO.getPosY());
        existente.setEscala(categoriaDTO.getEscala());
        existente.setRotacion(categoriaDTO.getRotacion());
        existente.setVolteoH(categoriaDTO.getVolteoH());
        existente.setVolteoV(categoriaDTO.getVolteoV());

        // 🔥 EL SECRETO: Al no tocar `existente.setOrden(...)`,
        // Hibernate mantiene intacto el orden original en la base de datos.

        CategoriaProducto actualizada = categoriaRepository.save(existente);
        return modelMapper.map(actualizada, CategoriaProductoDTO.class);
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // Solo la "apagamos", Sami la puede volver a prender luego si quiere
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void eliminarFisico(Long id) {
        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // 🔥 MAGIA ANTI-ERRORES: Si la categoría tiene productos, los soltamos primero
        // Esto evita que PostgreSQL explote por intentar borrar algo que está en uso.
        if (categoria.getProductos() != null) {
            categoria.getProductos().forEach(producto -> {
                producto.getCategorias().remove(categoria);
            });
        }

        // Ahora sí, la borramos para siempre de la base de datos
        categoriaRepository.delete(categoria);
    }

    @Override
    @Transactional
    public void reordenarLote(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long id = idsOrdenados.get(i);
            int nuevoOrden = i + 1; // La posición en la lista de Angular será el nuevo orden
            categoriaRepository.findById(id).ifPresent(c -> {
                c.setOrden(nuevoOrden);
                categoriaRepository.save(c);
            });
        }
    }
}
