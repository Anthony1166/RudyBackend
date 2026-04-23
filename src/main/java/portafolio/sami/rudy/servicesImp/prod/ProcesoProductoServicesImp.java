package portafolio.sami.rudy.servicesImp.prod;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portafolio.sami.rudy.dto.prod.ProcesoProductoDTO;
import portafolio.sami.rudy.entities.prod.ProcesoProducto;
import portafolio.sami.rudy.entities.prod.Producto;
import portafolio.sami.rudy.repositories.prod.ProcesoProductoRepository;
import portafolio.sami.rudy.repositories.prod.ProductoRepository;
import portafolio.sami.rudy.services.prod.ProcesoProductoServices;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcesoProductoServicesImp implements ProcesoProductoServices {

    private final ProcesoProductoRepository procesoRepository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProcesoProductoDTO> listarPorProducto(Long productoId) {
        return procesoRepository.findByProductoIdOrderByOrdenAsc(productoId).stream()
                .map(p -> modelMapper.map(p, ProcesoProductoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProcesoProductoDTO agregarAProducto(Long productoId, ProcesoProductoDTO dto) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        ProcesoProducto proceso = modelMapper.map(dto, ProcesoProducto.class);
        proceso.setProducto(producto);

        // 🔥 BLINDAJE ABSOLUTO: Si por alguna razón el orden llega nulo, lo forzamos.
        if (proceso.getOrden() == null || proceso.getOrden() <= 0) {
            Integer maxOrden = procesoRepository.obtenerMaximoOrdenPorProducto(productoId);
            proceso.setOrden(maxOrden != null ? maxOrden + 1 : 1);
        }

        ProcesoProducto guardado = procesoRepository.save(proceso);
        return modelMapper.map(guardado, ProcesoProductoDTO.class);
    }

    @Override
    @Transactional
    public ProcesoProductoDTO actualizar(Long id, ProcesoProductoDTO dto) {
        ProcesoProducto existente = procesoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paso de proceso no encontrado"));

        Long productoId = existente.getProducto().getId();

        existente.setTitulo(dto.getTitulo());
        existente.setDescripcion(dto.getDescripcion());
        existente.setUrlImagen(dto.getUrlImagen());

        // Magia del Empuje (Shift)
        Integer ordenAntiguo = existente.getOrden();
        Integer ordenNuevo = dto.getOrden();

        if (ordenNuevo != null && !ordenNuevo.equals(ordenAntiguo)) {
            if (ordenNuevo < ordenAntiguo) {
                List<ProcesoProducto> afectados = procesoRepository.findByProductoIdAndOrdenBetween(productoId, ordenNuevo, ordenAntiguo - 1);
                for (ProcesoProducto p : afectados) p.setOrden(p.getOrden() + 1);
                procesoRepository.saveAll(afectados);
            } else {
                List<ProcesoProducto> afectados = procesoRepository.findByProductoIdAndOrdenBetween(productoId, ordenAntiguo + 1, ordenNuevo);
                for (ProcesoProducto p : afectados) p.setOrden(p.getOrden() - 1);
                procesoRepository.saveAll(afectados);
            }
            existente.setOrden(ordenNuevo);
        }

        ProcesoProducto actualizado = procesoRepository.save(existente);
        return modelMapper.map(actualizado, ProcesoProductoDTO.class);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ProcesoProducto proceso = procesoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paso de proceso no encontrado"));
        procesoRepository.delete(proceso);
    }

    @Override
    @Transactional
    public void reordenarLote(List<Long> idsOrdenados) {
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Long id = idsOrdenados.get(i);
            int nuevoOrden = i + 1;

            procesoRepository.findById(id).ifPresent(p -> {
                p.setOrden(nuevoOrden);
                procesoRepository.save(p);
            });
        }
    }
}
