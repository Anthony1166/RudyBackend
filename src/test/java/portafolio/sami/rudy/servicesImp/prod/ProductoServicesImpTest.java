package portafolio.sami.rudy.servicesImp.prod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import portafolio.sami.rudy.dto.prod.CategoriaProductoDTO;
import portafolio.sami.rudy.dto.prod.ProductoDTO;
import portafolio.sami.rudy.entities.prod.CategoriaProducto;
import portafolio.sami.rudy.entities.prod.ImagenProducto;
import portafolio.sami.rudy.entities.prod.Producto;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.prod.CategoriaProductoRepository;
import portafolio.sami.rudy.repositories.prod.ProductoRepository;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoServicesImp")
class ProductoServicesImpTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private CategoriaProductoRepository categoriaRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private S3Service s3Service;

    @InjectMocks
    private ProductoServicesImp service;

    private Producto productoExistente;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        productoExistente = new Producto();
        productoExistente.setId(1L);
        productoExistente.setNombre("Taza de Cerámica");
        productoExistente.setSlug("taza-de-ceramica");
        productoExistente.setActivo(true);
        productoExistente.setOrden(1);
        productoExistente.setImagenes(new ArrayList<>());
        productoExistente.setProcesos(new ArrayList<>());
        productoExistente.setCategorias(new ArrayList<>());

        productoDTO = new ProductoDTO();
        productoDTO.setNombre("Taza de Cerámica");
        productoDTO.setPrecio(25.0);
        productoDTO.setDescripcion("Una taza artesanal");
    }

    // ── listarActivos ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarActivos retorna lista mapeada en orden")
    void listarActivos_retornaListaMapeada() {
        when(productoRepository.findAllByActivoTrueOrderByOrdenAsc()).thenReturn(List.of(productoExistente));
        when(modelMapper.map(productoExistente, ProductoDTO.class)).thenReturn(productoDTO);

        List<ProductoDTO> resultado = service.listarActivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Taza de Cerámica");
    }

    // ── obtenerPorId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId retorna DTO cuando el producto existe")
    void obtenerPorId_cuandoExiste_retornaDTO() {
        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(productoExistente));
        when(modelMapper.map(productoExistente, ProductoDTO.class)).thenReturn(productoDTO);

        ProductoDTO resultado = service.obtenerPorId(1L);

        assertThat(resultado.getNombre()).isEqualTo("Taza de Cerámica");
    }

    @Test
    @DisplayName("obtenerPorId lanza ResourceNotFoundException cuando no existe")
    void obtenerPorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── buscarProductos ───────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarProductos con término vacío delega a listarActivos")
    void buscarProductos_terminoVacio_delegaAListarActivos() {
        when(productoRepository.findAllByActivoTrueOrderByOrdenAsc()).thenReturn(List.of(productoExistente));
        when(modelMapper.map(productoExistente, ProductoDTO.class)).thenReturn(productoDTO);

        List<ProductoDTO> resultado = service.buscarProductos("");

        assertThat(resultado).hasSize(1);
        verify(productoRepository, never()).buscarNivelDios(any());
    }

    @Test
    @DisplayName("buscarProductos con término llama al repositorio de búsqueda")
    void buscarProductos_conTermino_llamaAlRepositorio() {
        when(productoRepository.buscarNivelDios("taza")).thenReturn(List.of(productoExistente));
        when(modelMapper.map(productoExistente, ProductoDTO.class)).thenReturn(productoDTO);

        List<ProductoDTO> resultado = service.buscarProductos("taza");

        assertThat(resultado).hasSize(1);
        verify(productoRepository).buscarNivelDios("taza");
    }

    // ── registrar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("registrar genera slug automático desde el nombre")
    void registrar_generaSlugDesdeNombre() {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Cántaro de Barro");
        dto.setPrecio(40.0);
        dto.setDescripcion("Artesanal");
        dto.setCategorias(new ArrayList<>());

        Producto entidad = new Producto();
        entidad.setId(2L);
        entidad.setNombre("Cántaro de Barro");

        when(modelMapper.map(dto, Producto.class)).thenReturn(entidad);
        when(productoRepository.obtenerMaximoOrden()).thenReturn(2);
        when(productoRepository.save(any())).thenReturn(entidad);
        when(modelMapper.map(entidad, ProductoDTO.class)).thenReturn(dto);

        service.registrar(dto);

        verify(productoRepository).save(argThat(p -> "cantaro-de-barro".equals(p.getSlug())));
    }

    @Test
    @DisplayName("registrar lanza ResourceNotFoundException si la categoría no existe")
    void registrar_categoriaNoExiste_lanzaResourceNotFoundException() {
        CategoriaProductoDTO catDTO = new CategoriaProductoDTO();
        catDTO.setId(99L);

        productoDTO.setCategorias(List.of(catDTO));

        Producto entidad = new Producto();
        entidad.setNombre("Taza de Cerámica");

        when(modelMapper.map(productoDTO, Producto.class)).thenReturn(entidad);
        when(productoRepository.obtenerMaximoOrden()).thenReturn(0);
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar(productoDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar lanza ResourceNotFoundException cuando no existe")
    void actualizar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, productoDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("actualizar regenera el slug cuando el nombre cambia")
    void actualizar_cuandoNombiaCambia_regeneraSlug() {
        ProductoDTO dtoNuevo = new ProductoDTO();
        dtoNuevo.setNombre("Jarro de Arcilla");
        dtoNuevo.setPrecio(30.0);
        dtoNuevo.setDescripcion("Artesanal");
        dtoNuevo.setCategorias(new ArrayList<>());

        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(productoExistente));
        when(productoRepository.save(any())).thenReturn(productoExistente);

        service.actualizar(1L, dtoNuevo);

        verify(productoRepository).save(argThat(p -> "jarro-de-arcilla".equals(p.getSlug())));
    }

    @Test
    @DisplayName("actualizar conserva el slug cuando el nombre no cambia")
    void actualizar_cuandoNombreNoCambia_conservaSlug() {
        ProductoDTO mismoNombre = new ProductoDTO();
        mismoNombre.setNombre("Taza de Cerámica");
        mismoNombre.setPrecio(25.0);
        mismoNombre.setDescripcion("desc");
        mismoNombre.setCategorias(new ArrayList<>());

        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(productoExistente));
        when(productoRepository.save(any())).thenReturn(productoExistente);

        service.actualizar(1L, mismoNombre);

        verify(productoRepository).save(argThat(p -> "taza-de-ceramica".equals(p.getSlug())));
    }

    // ── eliminarLogico ────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarLogico invierte el flag activo")
    void eliminarLogico_cuandoEstaActivo_setaActivoFalse() {
        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(productoExistente));

        service.eliminarLogico(1L);

        assertThat(productoExistente.getActivo()).isFalse();
        verify(productoRepository).save(productoExistente);
    }

    @Test
    @DisplayName("eliminarLogico lanza ResourceNotFoundException cuando no existe")
    void eliminarLogico_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarLogico(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── eliminarFisico ────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarFisico borra el producto de la BD")
    void eliminarFisico_cuandoExiste_eliminaDeLaBD() {
        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(productoExistente));

        service.eliminarFisico(1L);

        verify(productoRepository).delete(productoExistente);
    }

    @Test
    @DisplayName("eliminarFisico llama a S3 por cada imagen con URL")
    void eliminarFisico_conImagenes_borraArchivosDeS3() {
        ImagenProducto img = new ImagenProducto();
        img.setUrlImagen("https://cdn.example.com/foto.jpg");
        productoExistente.setImagenes(List.of(img));

        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(productoExistente));

        service.eliminarFisico(1L);

        verify(s3Service).deleteFile("foto.jpg");
    }

    @Test
    @DisplayName("eliminarFisico lanza ResourceNotFoundException cuando no existe")
    void eliminarFisico_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarFisico(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── reordenarLote ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("reordenarLote asigna orden correcto a cada producto")
    void reordenarLote_asignaOrdenCorrecto() {
        Producto p2 = new Producto();
        p2.setId(2L);

        when(productoRepository.findById(1L)).thenReturn(Optional.ofNullable(productoExistente));
        when(productoRepository.findById(2L)).thenReturn(Optional.ofNullable(p2));

        service.reordenarLote(List.of(1L, 2L));

        assertThat(productoExistente.getOrden()).isEqualTo(1);
        assertThat(p2.getOrden()).isEqualTo(2);
    }
}
