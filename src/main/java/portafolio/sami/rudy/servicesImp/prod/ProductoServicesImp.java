package portafolio.sami.rudy.servicesImp.prod;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.prod.CategoriaProductoDTO;
import portafolio.sami.rudy.dto.prod.ProductoDTO;
import portafolio.sami.rudy.entities.prod.CategoriaProducto;
import portafolio.sami.rudy.entities.prod.ImagenProducto;
import portafolio.sami.rudy.entities.prod.ProcesoProducto;
import portafolio.sami.rudy.entities.prod.Producto;
import portafolio.sami.rudy.repositories.prod.CategoriaProductoRepository;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.prod.ProductoRepository;
import portafolio.sami.rudy.services.prod.ProductoServices;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServicesImp implements ProductoServices {

    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;
    private final CategoriaProductoRepository categoriaRepository;
    private final S3Service s3Service;

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
    public List<ProductoDTO> listarActivos() {
        // CORRECCIÓN 1: Ahora sí usamos el que los trae ORDENADOS
        List<Producto> productos = productoRepository.findAllByActivoTrueOrderByOrdenAsc();
        return productos.stream()
                .map(p -> modelMapper.map(p, ProductoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarNuevos() {
        List<Producto> productos = productoRepository.findAllByActivoTrueOrderByFechaCreacionDesc();
        return productos.stream()
                .map(p -> modelMapper.map(p, ProductoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        return modelMapper.map(producto, ProductoDTO.class);
    }



    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarProductos(String termino) {
        // Si Sami no escribe nada, le devolvemos todos los activos
        if (termino == null || termino.trim().isEmpty()) {
            return listarActivos();
        }

        List<Producto> encontrados = productoRepository.buscarNivelDios(termino);

        return encontrados.stream()
                .map(p -> modelMapper.map(p, ProductoDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ProductoDTO registrar(ProductoDTO productoDTO) {
        Producto producto = modelMapper.map(productoDTO, Producto.class);

        if (productoDTO.getActivo() != null) {
            producto.setActivo(productoDTO.getActivo());
        } else {
            producto.setActivo(true);
        }

        producto.setSlug(generarSlug(productoDTO.getNombre())); // Slug automático

        if (productoDTO.getDestacado() != null) {
            producto.setDestacado(productoDTO.getDestacado());
        } else {
            producto.setDestacado(false);
        }

        // Orden automático si no lo enviaron
        if (productoDTO.getOrden() == null) {
            producto.setOrden(productoRepository.obtenerMaximoOrden() + 1);
        }

        // Buscamos las categorías en la BD para evitar el error "detached entity"
        if (productoDTO.getCategorias() != null && !productoDTO.getCategorias().isEmpty()) {
            List<CategoriaProducto> categoriasReales = productoDTO.getCategorias().stream()
                    .map(cat -> categoriaRepository.findById(cat.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada")))
                    .collect(Collectors.toList());
            producto.setCategorias(categoriasReales);
        } else {
            producto.setCategorias(new ArrayList<>());
        }

        Producto productoGuardado = productoRepository.save(producto);
        return modelMapper.map(productoGuardado, ProductoDTO.class);
    }

    @Override
    @Transactional
    public ProductoDTO actualizar(Long id, ProductoDTO productoDTO) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // 1. RESCATE DE DATOS ORIGINALES
        Integer ordenOriginal = existente.getOrden();
        String slugOriginal = existente.getSlug();
        String nombreOriginal = existente.getNombre();

        List<ImagenProducto> imagenesOriginales = existente.getImagenes();
        List<ProcesoProducto> procesosOriginales = existente.getProcesos();
        List<CategoriaProductoDTO> categoriasNuevas = productoDTO.getCategorias();

        // 🔥 LA BARRERA DE HIERRO: Escondemos TODAS las colecciones al ModelMapper.
        // Así evitamos que intente cruzar IDs y ensucie la sesión de Hibernate.
        productoDTO.setCategorias(null);
        productoDTO.setImagenes(null); // <-- ¡NUEVO!
        productoDTO.setProcesos(null); // <-- ¡NUEVO!

        // 2. EL MODELMAPPER HACE EL TRABAJO PESADO (Ahora es 100% ciego a las listas)
        modelMapper.map(productoDTO, existente);

        // 3. BLINDAJE ABSOLUTO: Restauramos las colecciones originales
        existente.setOrden(ordenOriginal);
        existente.setImagenes(imagenesOriginales);
        existente.setProcesos(procesosOriginales);

        // 4. LÓGICA INTELIGENTE DEL SLUG
        if (!nombreOriginal.equals(productoDTO.getNombre())) {
            existente.setSlug(generarSlug(productoDTO.getNombre()));
        } else {
            existente.setSlug(slugOriginal);
        }

        // 5. PROTECCIÓN PARA EL DESTACADO
        if (productoDTO.getDestacado() != null) {
            existente.setDestacado(productoDTO.getDestacado());
        }

        // 6. ASIGNACIÓN DE CATEGORÍAS REALES
        if (categoriasNuevas != null && !categoriasNuevas.isEmpty()) {
            List<CategoriaProducto> categoriasReales = categoriasNuevas.stream()
                    .map(catDTO -> categoriaRepository.findById(catDTO.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada")))
                    .collect(Collectors.toList());
            existente.setCategorias(categoriasReales);
        } else {
            existente.setCategorias(new ArrayList<>());
        }

        // 7. GUARDAMOS Y RETORNAMOS
        Producto productoGuardado = productoRepository.save(existente);
        return modelMapper.map(productoGuardado, ProductoDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodosAdmin() {
        return productoRepository.findAllByOrderByOrdenAsc().stream()
                .map(p -> modelMapper.map(p, ProductoDTO.class))
                .collect(Collectors.toList());
    }
    // 🔥 CORRECCIÓN 2: El metodo para filtrar por categoría respetando el orden global
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoria(String categoriaSlug) {
        List<Producto> productos = productoRepository.findByCategorias_SlugAndActivoTrueOrderByOrdenAsc(categoriaSlug);
        return productos.stream()
                .map(p -> modelMapper.map(p, ProductoDTO.class))
                .collect(Collectors.toList());
    }

    // 🔥 CORRECCIÓN 3: El metodo para que Angular pida el detalle del producto usando la URL amigable
    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorSlug(String slug) {
        Producto producto = productoRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el slug: " + slug));
        return modelMapper.map(producto, ProductoDTO.class);
    }

    @Override
    @Transactional
    public void eliminarLogico(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // ¡El salvavidas! Solo lo apagamos, no hacemos productoRepository.delete(producto)
        producto.setActivo(!producto.getActivo());
        productoRepository.save(producto);
    }
    @Override
    @Transactional
    public void eliminarFisico(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Limpiar archivos de R2 antes de borrar de la BD
        for (ImagenProducto img : producto.getImagenes()) {
            borrarArchivoR2(img.getUrlImagen());
        }
        for (ProcesoProducto proc : producto.getProcesos()) {
            borrarArchivoR2(proc.getUrlImagen());
        }

        productoRepository.delete(producto);
    }

    private void borrarArchivoR2(String url) {
        if (url == null || url.isBlank()) return;
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            s3Service.deleteFile(fileName);
        } catch (Exception ignored) {
            // Si el archivo ya no existe en R2, continuamos sin interrumpir el borrado
        }
    }


    @Override
    @Transactional
    public void reordenarLote(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long id = idsOrdenados.get(i);
            int nuevoOrden = i + 1;
            productoRepository.findById(id).ifPresent(p -> {
                p.setOrden(nuevoOrden);
                productoRepository.save(p);
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarPorCategoriaAdmin(Long categoriaId) {
        return productoRepository.findByCategoriaIdParaAdmin(categoriaId).stream()
                .map(p -> modelMapper.map(p, ProductoDTO.class))
                .collect(Collectors.toList());
    }
}
