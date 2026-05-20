package portafolio.sami.rudy.servicesImp.proy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import portafolio.sami.rudy.dto.proy.CategoriaProyectoDTO;
import portafolio.sami.rudy.entities.proy.CategoriaProyecto;
import portafolio.sami.rudy.exceptions.BusinessException;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.proy.CategoriaProyectoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaProyectoServicesImp")
class CategoriaProyectoServicesImpTest {

    @Mock private CategoriaProyectoRepository categoriaRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private CategoriaProyectoServicesImp service;

    private CategoriaProyecto categoriaExistente;
    private CategoriaProyectoDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoriaExistente = new CategoriaProyecto();
        categoriaExistente.setIdCategoria(1L);
        categoriaExistente.setNombre("Industrial");
        categoriaExistente.setSlug("industrial");
        categoriaExistente.setActivo(true);
        categoriaExistente.setOrden(1);

        categoriaDTO = new CategoriaProyectoDTO();
        categoriaDTO.setNombre("Industrial");
    }

    // ── listarTodas ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodas retorna lista mapeada en orden")
    void listarTodas_retornaListaMapeada() {
        when(categoriaRepository.findAllByOrderByOrdenAsc()).thenReturn(List.of(categoriaExistente));
        when(modelMapper.map(categoriaExistente, CategoriaProyectoDTO.class)).thenReturn(categoriaDTO);

        List<CategoriaProyectoDTO> resultado = service.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Industrial");
    }

    // ── obtenerPorId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId retorna DTO cuando la categoría existe")
    void obtenerPorId_cuandoExiste_retornaDTO() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoriaExistente));
        when(modelMapper.map(categoriaExistente, CategoriaProyectoDTO.class)).thenReturn(categoriaDTO);

        CategoriaProyectoDTO resultado = service.obtenerPorId(1L);

        assertThat(resultado.getNombre()).isEqualTo("Industrial");
    }

    @Test
    @DisplayName("obtenerPorId lanza ResourceNotFoundException cuando no existe")
    void obtenerPorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no encontrada");
    }

    // ── guardar ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("guardar crea la categoría y genera el slug correctamente")
    void guardar_cuandoNombreLibre_guardaYGeneraSlug() {
        CategoriaProyectoDTO dto = new CategoriaProyectoDTO();
        dto.setNombre("Diseño Editorial");

        CategoriaProyecto entidadGuardada = new CategoriaProyecto();
        entidadGuardada.setIdCategoria(2L);
        entidadGuardada.setNombre("Diseño Editorial");
        entidadGuardada.setSlug("diseno-editorial");

        when(categoriaRepository.existsByNombre("Diseño Editorial")).thenReturn(false);
        when(categoriaRepository.findBySlug("diseno-editorial")).thenReturn(Optional.empty());
        when(categoriaRepository.obtenerMaximoOrden()).thenReturn(2);
        when(modelMapper.map(dto, CategoriaProyecto.class)).thenReturn(entidadGuardada);
        when(categoriaRepository.save(any())).thenReturn(entidadGuardada);
        when(modelMapper.map(entidadGuardada, CategoriaProyectoDTO.class)).thenReturn(dto);

        service.guardar(dto);

        verify(categoriaRepository).save(argThat(c -> "diseno-editorial".equals(c.getSlug())));
    }

    @Test
    @DisplayName("guardar lanza BusinessException cuando el nombre ya existe")
    void guardar_cuandoNombreOcupado_lanzaBusinessException() {
        when(categoriaRepository.existsByNombre("Industrial")).thenReturn(true);

        assertThatThrownBy(() -> service.guardar(categoriaDTO))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("guardar lanza BusinessException cuando el slug ya existe")
    void guardar_cuandoSlugOcupado_lanzaBusinessException() {
        when(categoriaRepository.existsByNombre("Industrial")).thenReturn(false);
        when(categoriaRepository.findBySlug("industrial")).thenReturn(Optional.ofNullable(categoriaExistente));

        assertThatThrownBy(() -> service.guardar(categoriaDTO))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("guardar genera slug correcto con tildes y espacios")
    void guardar_conTildesYEspacios_generaSlugLimpio() {
        CategoriaProyectoDTO dto = new CategoriaProyectoDTO();
        dto.setNombre("Ergonomía & Diseño");

        CategoriaProyecto entidad = new CategoriaProyecto();
        entidad.setIdCategoria(3L);

        when(categoriaRepository.existsByNombre("Ergonomía & Diseño")).thenReturn(false);
        when(categoriaRepository.findBySlug("ergonomia-diseno")).thenReturn(Optional.empty());
        when(categoriaRepository.obtenerMaximoOrden()).thenReturn(0);
        when(modelMapper.map(dto, CategoriaProyecto.class)).thenReturn(entidad);
        when(categoriaRepository.save(any())).thenReturn(entidad);
        when(modelMapper.map(entidad, CategoriaProyectoDTO.class)).thenReturn(dto);

        service.guardar(dto);

        verify(categoriaRepository).save(argThat(c -> "ergonomia-diseno".equals(c.getSlug())));
    }

    // ── actualizar ────────────────────────────────────────────────────────────

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
        CategoriaProyectoDTO dtoNuevo = new CategoriaProyectoDTO();
        dtoNuevo.setNombre("Ergonomía");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoriaExistente));
        when(categoriaRepository.existsByNombre("Ergonomía")).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, dtoNuevo))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("actualizar no valida slug cuando el nombre no cambia")
    void actualizar_cuandoNombreNoCambia_noValidaSlug() {
        CategoriaProyectoDTO mismoNombre = new CategoriaProyectoDTO();
        mismoNombre.setNombre("Industrial");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoriaExistente));
        when(categoriaRepository.save(any())).thenReturn(categoriaExistente);
        when(modelMapper.map(categoriaExistente, CategoriaProyectoDTO.class)).thenReturn(mismoNombre);

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
        categoriaExistente.setProyectos(new ArrayList<>());
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
