package portafolio.sami.rudy.servicesImp.prod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import portafolio.sami.rudy.dto.prod.ImagenProductoDTO;
import portafolio.sami.rudy.entities.prod.ImagenProducto;
import portafolio.sami.rudy.entities.prod.Producto;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.prod.ImagenProductoRepository;
import portafolio.sami.rudy.repositories.prod.ProductoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImagenProductoServicesImp")
class ImagenProductoServicesImpTest {

    @Mock private ImagenProductoRepository imagenRepository;
    @Mock private ProductoRepository productoRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private ImagenProductoServicesImp service;

    private Producto producto;
    private ImagenProducto imagenExistente;
    private ImagenProductoDTO imagenDTO;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Taza");

        imagenExistente = new ImagenProducto();
        imagenExistente.setId(10L);
        imagenExistente.setUrlImagen("https://cdn.example.com/foto.jpg");
        imagenExistente.setEsPortada(false);
        imagenExistente.setOrden(1);
        imagenExistente.setProducto(producto);

        imagenDTO = new ImagenProductoDTO();
        imagenDTO.setUrlImagen("https://cdn.example.com/foto.jpg");
        imagenDTO.setEsPortada(false);
    }

    // ── listarPorProducto ─────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorProducto retorna lista mapeada")
    void listarPorProducto_retornaListaMapeada() {
        when(imagenRepository.findByProductoIdOrderByOrdenAsc(1L)).thenReturn(List.of(imagenExistente));
        when(modelMapper.map(imagenExistente, ImagenProductoDTO.class)).thenReturn(imagenDTO);

        List<ImagenProductoDTO> resultado = service.listarPorProducto(1L);

        assertThat(resultado).hasSize(1);
    }

    // ── agregarAProducto ──────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarAProducto lanza ResourceNotFoundException cuando el producto no existe")
    void agregarAProducto_productoNoExiste_lanzaResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.agregarAProducto(99L, imagenDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("agregarAProducto asigna el producto y calcula el orden")
    void agregarAProducto_asignaProductoYOrden() {
        ImagenProducto nuevaImagen = new ImagenProducto();

        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(producto));
        when(modelMapper.map(imagenDTO, ImagenProducto.class)).thenReturn(nuevaImagen);
        when(imagenRepository.obtenerMaximoOrdenPorProducto(1L)).thenReturn(3);
        when(imagenRepository.save(any())).thenReturn(nuevaImagen);
        when(modelMapper.map(nuevaImagen, ImagenProductoDTO.class)).thenReturn(imagenDTO);

        service.agregarAProducto(1L, imagenDTO);

        assertThat(nuevaImagen.getProducto()).isEqualTo(producto);
        assertThat(nuevaImagen.getOrden()).isEqualTo(4);
    }

    @Test
    @DisplayName("agregarAProducto destituye la portada anterior cuando la nueva es portada")
    void agregarAProducto_nuevaEsPortada_destituyePortadaAnterior() {
        imagenDTO.setEsPortada(true);

        ImagenProducto portadaAnterior = new ImagenProducto();
        portadaAnterior.setEsPortada(true);

        ImagenProducto nuevaImagen = new ImagenProducto();
        nuevaImagen.setEsPortada(true);

        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(producto));
        when(modelMapper.map(imagenDTO, ImagenProducto.class)).thenReturn(nuevaImagen);
        when(imagenRepository.findByProductoIdAndEsPortadaTrue(1L)).thenReturn(Optional.of(portadaAnterior));
        when(imagenRepository.obtenerMaximoOrdenPorProducto(1L)).thenReturn(1);
        when(imagenRepository.save(any())).thenReturn(nuevaImagen);
        when(modelMapper.map(nuevaImagen, ImagenProductoDTO.class)).thenReturn(imagenDTO);

        service.agregarAProducto(1L, imagenDTO);

        assertThat(portadaAnterior.getEsPortada()).isFalse();
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar lanza ResourceNotFoundException cuando la imagen no existe")
    void actualizar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(imagenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, imagenDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("actualizar actualiza textoAlternativo y urlImagen")
    void actualizar_actualizaCampos() {
        imagenDTO.setTextoAlternativo("Nueva descripción");
        imagenDTO.setUrlImagen("https://cdn.example.com/nueva.jpg");

        when(imagenRepository.findById(10L)).thenReturn(Optional.ofNullable(imagenExistente));
        when(imagenRepository.save(any())).thenReturn(imagenExistente);
        when(modelMapper.map(imagenExistente, ImagenProductoDTO.class)).thenReturn(imagenDTO);

        service.actualizar(10L, imagenDTO);

        assertThat(imagenExistente.getTextoAlternativo()).isEqualTo("Nueva descripción");
        assertThat(imagenExistente.getUrlImagen()).isEqualTo("https://cdn.example.com/nueva.jpg");
    }

    // ── eliminar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar borra la imagen de la BD")
    void eliminar_cuandoExiste_eliminaDeLaBD() {
        when(imagenRepository.findById(10L)).thenReturn(Optional.ofNullable(imagenExistente));

        service.eliminar(10L);

        verify(imagenRepository).delete(imagenExistente);
    }

    @Test
    @DisplayName("eliminar lanza ResourceNotFoundException cuando no existe")
    void eliminar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(imagenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── reordenarLote ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("reordenarLote asigna orden correcto a cada imagen")
    void reordenarLote_asignaOrdenCorrecto() {
        ImagenProducto img2 = new ImagenProducto();
        img2.setId(20L);

        when(imagenRepository.findById(10L)).thenReturn(Optional.ofNullable(imagenExistente));
        when(imagenRepository.findById(20L)).thenReturn(Optional.ofNullable(img2));

        service.reordenarLote(List.of(10L, 20L));

        assertThat(imagenExistente.getOrden()).isEqualTo(1);
        assertThat(img2.getOrden()).isEqualTo(2);
    }
}
