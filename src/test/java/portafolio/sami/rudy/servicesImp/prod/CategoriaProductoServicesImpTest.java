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
import portafolio.sami.rudy.entities.prod.CategoriaProducto;
import portafolio.sami.rudy.exceptions.BusinessException;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.prod.CategoriaProductoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaProductoServicesImp")
class CategoriaProductoServicesImpTest {

    @Mock
    private CategoriaProductoRepository categoriaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoriaProductoServicesImp service;

    private CategoriaProducto categoriaExistente;
    private CategoriaProductoDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoriaExistente = new CategoriaProducto();
        categoriaExistente.setId(1L);
        categoriaExistente.setNombre("Bolsos");
        categoriaExistente.setSlug("bolsos");
        categoriaExistente.setActivo(true);
        categoriaExistente.setOrden(1);

        categoriaDTO = new CategoriaProductoDTO();
        categoriaDTO.setNombre("Bolsos");
    }

    // ── listarTodas ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodas retorna lista mapeada en orden")
    void listarTodas_retornaListaMapeada() {
        when(categoriaRepository.findAllByOrderByOrdenAsc()).thenReturn(List.of(categoriaExistente));
        when(modelMapper.map(categoriaExistente, CategoriaProductoDTO.class)).thenReturn(categoriaDTO);

        List<CategoriaProductoDTO> resultado = service.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Bolsos");
    }

    // ── obtenerPorId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId retorna DTO cuando la categoría existe")
    void obtenerPorId_cuandoExiste_retornaDTO() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoriaExistente));
        when(modelMapper.map(categoriaExistente, CategoriaProductoDTO.class)).thenReturn(categoriaDTO);

        CategoriaProductoDTO resultado = service.obtenerPorId(1L);

        assertThat(resultado.getNombre()).isEqualTo("Bolsos");
    }

    @Test
    @DisplayName("obtenerPorId lanza ResourceNotFoundException cuando no existe")
    void obtenerPorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no encontrada");
    }

    // ── guardar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("guardar crea la categoría y genera el slug correctamente")
    void guardar_cuandoNombreLibre_guardaYGeneraSlug() {
        CategoriaProductoDTO dto = new CategoriaProductoDTO();
        dto.setNombre("Carteras de Cuero");

        CategoriaProducto entidadGuardada = new CategoriaProducto();
        entidadGuardada.setId(2L);
        entidadGuardada.setNombre("Carteras de Cuero");
        entidadGuardada.setSlug("carteras-de-cuero");

        when(categoriaRepository.existsByNombre("Carteras de Cuero")).thenReturn(false);
        when(categoriaRepository.findBySlug("carteras-de-cuero")).thenReturn(Optional.empty());
        when(categoriaRepository.obtenerMaximoOrden()).thenReturn(3);
        when(modelMapper.map(dto, CategoriaProducto.class)).thenReturn(entidadGuardada);
        when(categoriaRepository.save(any())).thenReturn(entidadGuardada);
        when(modelMapper.map(entidadGuardada, CategoriaProductoDTO.class)).thenReturn(dto);

        CategoriaProductoDTO resultado = service.guardar(dto);

        assertThat(resultado).isNotNull();
        verify(categoriaRepository).save(argThat(c -> "carteras-de-cuero".equals(c.getSlug())));
    }

    @Test
    @DisplayName("guardar lanza BusinessException cuando el nombre ya existe")
    void guardar_cuandoNombreOcupado_lanzaBusinessException() {
        categoriaDTO.setNombre("Bolsos");
        when(categoriaRepository.existsByNombre("Bolsos")).thenReturn(true);

        assertThatThrownBy(() -> service.guardar(categoriaDTO))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("guardar lanza BusinessException cuando el slug ya existe")
    void guardar_cuandoSlugOcupado_lanzaBusinessException() {
        categoriaDTO.setNombre("Bolsos");
        when(categoriaRepository.existsByNombre("Bolsos")).thenReturn(false);
        when(categoriaRepository.findBySlug("bolsos")).thenReturn(Optional.ofNullable(categoriaExistente));

        assertThatThrownBy(() -> service.guardar(categoriaDTO))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("guardar genera slug correcto con tildes y espacios")
    void guardar_conTildesYEspacios_generaSlugLimpio() {
        CategoriaProductoDTO dto = new CategoriaProductoDTO();
        dto.setNombre("Ñoñerías & Más");

        CategoriaProducto entidad = new CategoriaProducto();
        entidad.setId(3L);

        when(categoriaRepository.existsByNombre("Ñoñerías & Más")).thenReturn(false);
        when(categoriaRepository.findBySlug("nonerias-mas")).thenReturn(Optional.empty());
        when(categoriaRepository.obtenerMaximoOrden()).thenReturn(0);
        when(modelMapper.map(dto, CategoriaProducto.class)).thenReturn(entidad);
        when(categoriaRepository.save(any())).thenReturn(entidad);
        when(modelMapper.map(entidad, CategoriaProductoDTO.class)).thenReturn(dto);

        service.guardar(dto);

        verify(categoriaRepository).save(argThat(c -> "nonerias-mas".equals(c.getSlug())));
    }

    // ── actualizar ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar lanza ResourceNotFoundException cuando no existe")
    void actualizar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, categoriaDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("actualizar lanza BusinessException cuando el nuevo nombre ya está en uso")
    void actualizar_cuandoNombreCambia_yConflicto_lanzaBusinessException() {
        CategoriaProductoDTO dtoNuevo = new CategoriaProductoDTO();
        dtoNuevo.setNombre("Mochilas");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoriaExistente));
        when(categoriaRepository.existsByNombre("Mochilas")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, dtoNuevo))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("actualizar no valida slug cuando el nombre no cambia")
    void actualizar_cuandoNombreNocambia_noValidaSlug() {
        CategoriaProductoDTO mismoNombre = new CategoriaProductoDTO();
        mismoNombre.setNombre("Bolsos");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoriaExistente));
        when(categoriaRepository.save(any())).thenReturn(categoriaExistente);
        when(modelMapper.map(categoriaExistente, CategoriaProductoDTO.class)).thenReturn(mismoNombre);

        service.actualizar(1L, mismoNombre);

        verify(categoriaRepository, never()).existsByNombre(any());
        verify(categoriaRepository, never()).findBySlug(any());
    }

    // ── eliminarLogico ────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarLogico pone activo=false sin borrar el registro")
    void eliminarLogico_cuandoExiste_setaActivoFalse() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoriaExistente));

        service.eliminarLogico(1L);

        assertThat(categoriaExistente.getActivo()).isFalse();
        verify(categoriaRepository).save(categoriaExistente);
    }

    @Test
    @DisplayName("eliminarLogico lanza ResourceNotFoundException cuando no existe")
    void eliminarLogico_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarLogico(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── eliminarFisico ────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarFisico borra el registro de la base de datos")
    void eliminarFisico_cuandoExiste_eliminaDeLaBD() {
        categoriaExistente.setProductos(List.of());
        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoriaExistente));

        service.eliminarFisico(1L);

        verify(categoriaRepository).delete(categoriaExistente);
    }

    @Test
    @DisplayName("eliminarFisico lanza ResourceNotFoundException cuando no existe")
    void eliminarFisico_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarFisico(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
