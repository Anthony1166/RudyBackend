package portafolio.sami.rudy.servicesImp.prod;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.prod.ImagenProductoDTO;
import portafolio.sami.rudy.entities.prod.ImagenProducto;
import portafolio.sami.rudy.entities.prod.Producto;
import portafolio.sami.rudy.repositories.prod.ImagenProductoRepository;
import portafolio.sami.rudy.repositories.prod.ProductoRepository;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.services.prod.ImagenProductoServices;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImagenProductoServicesImp implements ImagenProductoServices {

    private final ImagenProductoRepository imagenRepository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    // 🔥 HERRAMIENTA PRIVADA: Gestor de Portadas
    private void gestionarPortada(Long productoId, Boolean nuevaEsPortada) {
        if (nuevaEsPortada != null && nuevaEsPortada) {
            // Si la nueva imagen quiere ser portada, buscamos a la antigua y la destituimos
            imagenRepository.findByProductoIdAndEsPortadaTrue(productoId).ifPresent(antigua -> {
                antigua.setEsPortada(false);
                imagenRepository.save(antigua);
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImagenProductoDTO> listarPorProducto(Long productoId) {
        return imagenRepository.findByProductoIdOrderByOrdenAsc(productoId).stream()
                .map(i -> modelMapper.map(i, ImagenProductoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ImagenProductoDTO agregarAProducto(Long productoId, ImagenProductoDTO dto) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        ImagenProducto imagen = modelMapper.map(dto, ImagenProducto.class);
        imagen.setProducto(producto);

        // 🔥 Aplicamos el Alt Text
        imagen.setTextoAlternativo(dto.getTextoAlternativo());

        if (dto.getEsPortada() == null) imagen.setEsPortada(false);
        gestionarPortada(productoId, imagen.getEsPortada());

        if (dto.getOrden() == null) {
            imagen.setOrden(imagenRepository.obtenerMaximoOrdenPorProducto(productoId) + 1);
        }

        ImagenProducto guardada = imagenRepository.save(imagen);
        return modelMapper.map(guardada, ImagenProductoDTO.class);
    }

    @Override
    @Transactional
    public ImagenProductoDTO actualizar(Long id, ImagenProductoDTO dto) {
        ImagenProducto existente = imagenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada"));

        Long productoId = existente.getProducto().getId();

        existente.setTextoAlternativo(dto.getTextoAlternativo());
        existente.setUrlImagen(dto.getUrlImagen());


        // 1. Magia de Portada al editar
        if (dto.getEsPortada() != null && !dto.getEsPortada().equals(existente.getEsPortada())) {
            gestionarPortada(productoId, dto.getEsPortada());
            existente.setEsPortada(dto.getEsPortada());
        }

        // 2. Magia del Empuje (Shift) al reordenar
        Integer ordenAntiguo = existente.getOrden();
        Integer ordenNuevo = dto.getOrden();

        if (ordenNuevo != null && !ordenNuevo.equals(ordenAntiguo)) {
            if (ordenNuevo < ordenAntiguo) {
                List<ImagenProducto> afectadas = imagenRepository.findByProductoIdAndOrdenBetween(productoId, ordenNuevo, ordenAntiguo - 1);
                for (ImagenProducto i : afectadas) i.setOrden(i.getOrden() + 1);
                imagenRepository.saveAll(afectadas);
            } else {
                List<ImagenProducto> afectadas = imagenRepository.findByProductoIdAndOrdenBetween(productoId, ordenAntiguo + 1, ordenNuevo);
                for (ImagenProducto i : afectadas) i.setOrden(i.getOrden() - 1);
                imagenRepository.saveAll(afectadas);
            }
            existente.setOrden(ordenNuevo);
        }

        ImagenProducto actualizada = imagenRepository.save(existente);
        return modelMapper.map(actualizada, ImagenProductoDTO.class);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ImagenProducto imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada"));
        imagenRepository.delete(imagen); // Destrucción física
    }

    // 🔥 EL NUEVO superpoder: Reordenamiento en Lote
    @Override
    @Transactional
    public void reordenarLote(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long id = idsOrdenados.get(i);
            int nuevoOrden = i + 1; // La posición en la lista define el orden

            imagenRepository.findById(id).ifPresent(img -> {
                img.setOrden(nuevoOrden);
                imagenRepository.save(img);
            });
        }
    }
}
