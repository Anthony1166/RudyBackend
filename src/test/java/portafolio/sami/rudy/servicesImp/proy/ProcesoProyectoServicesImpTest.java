package portafolio.sami.rudy.servicesImp.proy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import portafolio.sami.rudy.dto.proy.ProcesoProyectoDTO;
import portafolio.sami.rudy.entities.proy.ProcesoProyecto;
import portafolio.sami.rudy.entities.proy.Proyecto;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.proy.ProcesoProyectoRepository;
import portafolio.sami.rudy.repositories.proy.ProyectoRepository;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcesoProyectoServicesImp")
class ProcesoProyectoServicesImpTest {

    @Mock private ProcesoProyectoRepository procesoRepository;
    @Mock private ProyectoRepository proyectoRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private S3Service s3Service;

    @InjectMocks
    private ProcesoProyectoServicesImp service;

    private Proyecto proyecto;
    private ProcesoProyecto procesoExistente;
    private ProcesoProyectoDTO procesoDTO;

    @BeforeEach
    void setUp() {
        proyecto = new Proyecto();
        proyecto.setIdProyecto(1L);

        procesoExistente = new ProcesoProyecto();
        procesoExistente.setId(10L);
        procesoExistente.setTitulo("Bocetado");
        procesoExistente.setDescripcion("Fase de bocetado inicial");
        procesoExistente.setOrden(1);
        procesoExistente.setProyecto(proyecto);

        procesoDTO = new ProcesoProyectoDTO();
        procesoDTO.setTitulo("Bocetado");
        procesoDTO.setDescripcion("Fase de bocetado inicial");
    }

    // ── listarPorProyecto ─────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorProyecto retorna lista mapeada en orden")
    void listarPorProyecto_retornaListaMapeada() {
        when(procesoRepository.findByProyectoIdProyectoOrderByOrdenAsc(1L)).thenReturn(List.of(procesoExistente));
        when(modelMapper.map(procesoExistente, ProcesoProyectoDTO.class)).thenReturn(procesoDTO);

        List<ProcesoProyectoDTO> resultado = service.listarPorProyecto(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Bocetado");
    }

    // ── obtenerPorId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId retorna DTO cuando el proceso existe")
    void obtenerPorId_cuandoExiste_retornaDTO() {
        when(procesoRepository.findById(10L)).thenReturn(Optional.ofNullable(procesoExistente));
        when(modelMapper.map(procesoExistente, ProcesoProyectoDTO.class)).thenReturn(procesoDTO);

        ProcesoProyectoDTO resultado = service.obtenerPorId(10L);

        assertThat(resultado.getTitulo()).isEqualTo("Bocetado");
    }

    @Test
    @DisplayName("obtenerPorId lanza ResourceNotFoundException cuando no existe")
    void obtenerPorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(procesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── agregarAProyecto ──────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarAProyecto lanza ResourceNotFoundException cuando el proyecto no existe")
    void agregarAProyecto_proyectoNoExiste_lanzaResourceNotFoundException() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.agregarAProyecto(99L, procesoDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("agregarAProyecto calcula el orden como maxOrden+1")
    void agregarAProyecto_calculaOrden() {
        ProcesoProyecto nuevoProceso = new ProcesoProyecto();

        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyecto));
        when(modelMapper.map(procesoDTO, ProcesoProyecto.class)).thenReturn(nuevoProceso);
        when(procesoRepository.obtenerMaximoOrdenPorProyecto(1L)).thenReturn(2);
        when(procesoRepository.save(any())).thenReturn(nuevoProceso);
        when(modelMapper.map(nuevoProceso, ProcesoProyectoDTO.class)).thenReturn(procesoDTO);

        service.agregarAProyecto(1L, procesoDTO);

        assertThat(nuevoProceso.getOrden()).isEqualTo(3);
    }

    @Test
    @DisplayName("agregarAProyecto usa orden=1 cuando el repositorio retorna null")
    void agregarAProyecto_cuandoMaxOrdenNull_usaOrden1() {
        ProcesoProyecto nuevoProceso = new ProcesoProyecto();

        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyecto));
        when(modelMapper.map(procesoDTO, ProcesoProyecto.class)).thenReturn(nuevoProceso);
        when(procesoRepository.obtenerMaximoOrdenPorProyecto(1L)).thenReturn(null);
        when(procesoRepository.save(any())).thenReturn(nuevoProceso);
        when(modelMapper.map(nuevoProceso, ProcesoProyectoDTO.class)).thenReturn(procesoDTO);

        service.agregarAProyecto(1L, procesoDTO);

        assertThat(nuevoProceso.getOrden()).isEqualTo(1);
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar lanza ResourceNotFoundException cuando no existe")
    void actualizar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(procesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, procesoDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("actualizar actualiza título y descripción")
    void actualizar_actualizaCampos() {
        procesoDTO.setTitulo("Prototipado");
        procesoDTO.setDescripcion("Fase de prototipado");

        when(procesoRepository.findById(10L)).thenReturn(Optional.ofNullable(procesoExistente));
        when(procesoRepository.save(any())).thenReturn(procesoExistente);
        when(modelMapper.map(procesoExistente, ProcesoProyectoDTO.class)).thenReturn(procesoDTO);

        service.actualizar(10L, procesoDTO);

        assertThat(procesoExistente.getTitulo()).isEqualTo("Prototipado");
        assertThat(procesoExistente.getDescripcion()).isEqualTo("Fase de prototipado");
    }

    @Test
    @DisplayName("actualizar borra el archivo S3 anterior cuando la URL de imagen cambia")
    void actualizar_cuandoUrlCambia_borraArchivoS3Anterior() {
        procesoExistente.setUrlImagen("https://cdn.example.com/antigua.jpg");
        procesoDTO.setUrlImagen("https://cdn.example.com/nueva.jpg");
        procesoDTO.setTitulo("Bocetado");
        procesoDTO.setDescripcion("Descripción");

        when(procesoRepository.findById(10L)).thenReturn(Optional.ofNullable(procesoExistente));
        when(procesoRepository.save(any())).thenReturn(procesoExistente);
        when(modelMapper.map(procesoExistente, ProcesoProyectoDTO.class)).thenReturn(procesoDTO);

        service.actualizar(10L, procesoDTO);

        verify(s3Service).deleteFile("antigua.jpg");
    }

    // ── eliminar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar borra el proceso y el archivo de S3")
    void eliminar_cuandoExiste_eliminaDeLaBDYS3() {
        procesoExistente.setUrlImagen("https://cdn.example.com/boceto.jpg");
        when(procesoRepository.findById(10L)).thenReturn(Optional.ofNullable(procesoExistente));

        service.eliminar(10L);

        verify(s3Service).deleteFile("boceto.jpg");
        verify(procesoRepository).delete(procesoExistente);
    }

    @Test
    @DisplayName("eliminar lanza ResourceNotFoundException cuando no existe")
    void eliminar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(procesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── reordenarLote ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("reordenarLote asigna orden correcto a cada proceso")
    void reordenarLote_asignaOrdenCorrecto() {
        ProcesoProyecto p2 = new ProcesoProyecto();
        p2.setId(20L);

        when(procesoRepository.findById(10L)).thenReturn(Optional.ofNullable(procesoExistente));
        when(procesoRepository.findById(20L)).thenReturn(Optional.ofNullable(p2));

        service.reordenarLote(List.of(10L, 20L));

        assertThat(procesoExistente.getOrden()).isEqualTo(1);
        assertThat(p2.getOrden()).isEqualTo(2);
    }
}
