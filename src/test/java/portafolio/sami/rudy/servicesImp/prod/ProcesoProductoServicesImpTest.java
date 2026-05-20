package portafolio.sami.rudy.servicesImp.prod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import portafolio.sami.rudy.dto.prod.ProcesoProductoDTO;
import portafolio.sami.rudy.entities.prod.ProcesoProducto;
import portafolio.sami.rudy.entities.prod.Producto;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.prod.ProcesoProductoRepository;
import portafolio.sami.rudy.repositories.prod.ProductoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcesoProductoServicesImp")
class ProcesoProductoServicesImpTest {

    @Mock private ProcesoProductoRepository procesoRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private ProcesoProductoServicesImp service;

    private Producto producto;
    private ProcesoProducto procesoExistente;
    private ProcesoProductoDTO procesoDTO;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);

        procesoExistente = new ProcesoProducto();
        procesoExistente.setId(10L);
        procesoExistente.setTitulo("Modelado");
        procesoExistente.setDescripcion("Etapa de modelado inicial");
        procesoExistente.setOrden(1);
        procesoExistente.setProducto(producto);

        procesoDTO = new ProcesoProductoDTO();
        procesoDTO.setTitulo("Modelado");
        procesoDTO.setDescripcion("Etapa de modelado inicial");
    }

    // ── listarPorProducto ─────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorProducto retorna lista mapeada en orden")
    void listarPorProducto_retornaListaMapeada() {
        when(procesoRepository.findByProductoIdOrderByOrdenAsc(1L)).thenReturn(List.of(procesoExistente));
        when(modelMapper.map(procesoExistente, ProcesoProductoDTO.class)).thenReturn(procesoDTO);

        List<ProcesoProductoDTO> resultado = service.listarPorProducto(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Modelado");
    }

    // ── agregarAProducto ──────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarAProducto lanza ResourceNotFoundException cuando el producto no existe")
    void agregarAProducto_productoNoExiste_lanzaResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.agregarAProducto(99L, procesoDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("agregarAProducto calcula el orden como maxOrden+1")
    void agregarAProducto_calculaOrden() {
        ProcesoProducto nuevoProceso = new ProcesoProducto();

        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(producto));
        when(modelMapper.map(procesoDTO, ProcesoProducto.class)).thenReturn(nuevoProceso);
        when(procesoRepository.obtenerMaximoOrdenPorProducto(1L)).thenReturn(3);
        when(procesoRepository.save(any())).thenReturn(nuevoProceso);
        when(modelMapper.map(nuevoProceso, ProcesoProductoDTO.class)).thenReturn(procesoDTO);

        service.agregarAProducto(1L, procesoDTO);

        assertThat(nuevoProceso.getOrden()).isEqualTo(4);
    }

    @Test
    @DisplayName("agregarAProducto usa orden=1 cuando el repositorio retorna null")
    void agregarAProducto_cuandoMaxOrdenNull_usaOrden1() {
        ProcesoProducto nuevoProceso = new ProcesoProducto();

        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(producto));
        when(modelMapper.map(procesoDTO, ProcesoProducto.class)).thenReturn(nuevoProceso);
        when(procesoRepository.obtenerMaximoOrdenPorProducto(1L)).thenReturn(null);
        when(procesoRepository.save(any())).thenReturn(nuevoProceso);
        when(modelMapper.map(nuevoProceso, ProcesoProductoDTO.class)).thenReturn(procesoDTO);

        service.agregarAProducto(1L, procesoDTO);

        assertThat(nuevoProceso.getOrden()).isEqualTo(1);
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar lanza ResourceNotFoundException cuando el proceso no existe")
    void actualizar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(procesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, procesoDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    @DisplayName("actualizar actualiza título, descripción y urlImagen")
    void actualizar_actualizaCampos() {
        procesoDTO.setTitulo("Horneado");
        procesoDTO.setDescripcion("Etapa de horneado final");
        procesoDTO.setUrlImagen("https://cdn.example.com/horno.jpg");

        when(procesoRepository.findById(10L)).thenReturn(Optional.ofNullable(procesoExistente));
        when(procesoRepository.save(any())).thenReturn(procesoExistente);
        when(modelMapper.map(procesoExistente, ProcesoProductoDTO.class)).thenReturn(procesoDTO);

        service.actualizar(10L, procesoDTO);

        assertThat(procesoExistente.getTitulo()).isEqualTo("Horneado");
        assertThat(procesoExistente.getDescripcion()).isEqualTo("Etapa de horneado final");
    }

    // ── eliminar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar borra el proceso de la BD")
    void eliminar_cuandoExiste_eliminaDeLaBD() {
        when(procesoRepository.findById(10L)).thenReturn(Optional.ofNullable(procesoExistente));

        service.eliminar(10L);

        verify(procesoRepository).delete(procesoExistente);
    }

    @Test
    @DisplayName("eliminar lanza ResourceNotFoundException cuando no existe")
    void eliminar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(procesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── reordenarLote ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("reordenarLote asigna orden correcto a cada proceso")
    void reordenarLote_asignaOrdenCorrecto() {
        ProcesoProducto p2 = new ProcesoProducto();
        p2.setId(20L);

        when(procesoRepository.findById(10L)).thenReturn(Optional.ofNullable(procesoExistente));
        when(procesoRepository.findById(20L)).thenReturn(Optional.ofNullable(p2));

        service.reordenarLote(List.of(10L, 20L));

        assertThat(procesoExistente.getOrden()).isEqualTo(1);
        assertThat(p2.getOrden()).isEqualTo(2);
    }
}
