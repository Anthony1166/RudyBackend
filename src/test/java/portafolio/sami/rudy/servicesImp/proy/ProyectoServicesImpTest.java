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
import portafolio.sami.rudy.dto.proy.ProyectoDTO;
import portafolio.sami.rudy.entities.proy.ImagenProyecto;
import portafolio.sami.rudy.entities.proy.ProcesoProyecto;
import portafolio.sami.rudy.entities.proy.Proyecto;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.proy.CategoriaProyectoRepository;
import portafolio.sami.rudy.repositories.proy.ProyectoRepository;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProyectoServicesImp")
class ProyectoServicesImpTest {

    @Mock private ProyectoRepository proyectoRepository;
    @Mock private CategoriaProyectoRepository categoriaRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private S3Service s3Service;

    @InjectMocks
    private ProyectoServicesImp service;

    private Proyecto proyectoExistente;
    private ProyectoDTO proyectoDTO;

    @BeforeEach
    void setUp() {
        proyectoExistente = new Proyecto();
        proyectoExistente.setIdProyecto(1L);
        proyectoExistente.setTitulo("Silla Ergonómica");
        proyectoExistente.setSlug("silla-ergonomica");
        proyectoExistente.setActivo(true);
        proyectoExistente.setOrden(1);
        proyectoExistente.setImagenes(new ArrayList<>());
        proyectoExistente.setProcesosDiseno(new ArrayList<>());
        proyectoExistente.setCategorias(new ArrayList<>());

        proyectoDTO = new ProyectoDTO();
        proyectoDTO.setTitulo("Silla Ergonómica");
        proyectoDTO.setAnio(2024);
    }

    // ── listarActivos ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarActivos retorna lista mapeada")
    void listarActivos_retornaListaMapeada() {
        when(proyectoRepository.findAllActivosOrdenados()).thenReturn(List.of(proyectoExistente));
        when(modelMapper.map(proyectoExistente, ProyectoDTO.class)).thenReturn(proyectoDTO);

        List<ProyectoDTO> resultado = service.listarActivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Silla Ergonómica");
    }

    // ── obtenerPorId ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId retorna DTO cuando el proyecto existe")
    void obtenerPorId_cuandoExiste_retornaDTO() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyectoExistente));
        when(modelMapper.map(proyectoExistente, ProyectoDTO.class)).thenReturn(proyectoDTO);

        ProyectoDTO resultado = service.obtenerPorId(1L);

        assertThat(resultado.getTitulo()).isEqualTo("Silla Ergonómica");
    }

    @Test
    @DisplayName("obtenerPorId lanza ResourceNotFoundException cuando no existe")
    void obtenerPorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── obtenerPorSlug ────────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorSlug retorna DTO cuando el slug existe")
    void obtenerPorSlug_cuandoExiste_retornaDTO() {
        when(proyectoRepository.findBySlug("silla-ergonomica")).thenReturn(Optional.ofNullable(proyectoExistente));
        when(modelMapper.map(proyectoExistente, ProyectoDTO.class)).thenReturn(proyectoDTO);

        ProyectoDTO resultado = service.obtenerPorSlug("silla-ergonomica");

        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("obtenerPorSlug lanza ResourceNotFoundException cuando no existe")
    void obtenerPorSlug_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(proyectoRepository.findBySlug("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorSlug("no-existe"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── buscarProyectos ───────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarProyectos con término vacío delega a listarActivos")
    void buscarProyectos_terminoVacio_delegaAListarActivos() {
        when(proyectoRepository.findAllActivosOrdenados()).thenReturn(List.of(proyectoExistente));
        when(modelMapper.map(proyectoExistente, ProyectoDTO.class)).thenReturn(proyectoDTO);

        service.buscarProyectos(null);

        verify(proyectoRepository, never()).buscarNivelDios(any());
    }

    @Test
    @DisplayName("buscarProyectos con término llama al repositorio de búsqueda")
    void buscarProyectos_conTermino_llamaAlRepositorio() {
        when(proyectoRepository.buscarNivelDios("silla")).thenReturn(List.of(proyectoExistente));
        when(modelMapper.map(proyectoExistente, ProyectoDTO.class)).thenReturn(proyectoDTO);

        service.buscarProyectos("silla");

        verify(proyectoRepository).buscarNivelDios("silla");
    }

    // ── registrar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("registrar genera slug automático desde el título")
    void registrar_generaSlugDesdeElTitulo() {
        ProyectoDTO dto = new ProyectoDTO();
        dto.setTitulo("Lámpara Artesanal");
        dto.setAnio(2024);
        dto.setCategorias(new ArrayList<>());

        Proyecto entidad = new Proyecto();
        entidad.setIdProyecto(2L);
        entidad.setTitulo("Lámpara Artesanal");

        when(modelMapper.map(dto, Proyecto.class)).thenReturn(entidad);
        when(proyectoRepository.obtenerMaximoOrden()).thenReturn(1);
        when(proyectoRepository.save(any())).thenReturn(entidad);
        when(modelMapper.map(entidad, ProyectoDTO.class)).thenReturn(dto);

        service.registrar(dto);

        verify(proyectoRepository).save(argThat(p -> "lampara-artesanal".equals(p.getSlug())));
    }

    @Test
    @DisplayName("registrar lanza ResourceNotFoundException si la categoría no existe")
    void registrar_categoriaNoExiste_lanzaResourceNotFoundException() {
        CategoriaProyectoDTO catDTO = new CategoriaProyectoDTO();
        catDTO.setIdCategoria(99L);

        proyectoDTO.setCategorias(List.of(catDTO));

        Proyecto entidad = new Proyecto();
        entidad.setTitulo("Silla Ergonómica");

        when(modelMapper.map(proyectoDTO, Proyecto.class)).thenReturn(entidad);
        when(proyectoRepository.obtenerMaximoOrden()).thenReturn(0);
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registrar(proyectoDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── actualizar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar lanza ResourceNotFoundException cuando no existe")
    void actualizar_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, proyectoDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("actualizar regenera el slug cuando el título cambia")
    void actualizar_cuandoTituloCambia_regeneraSlug() {
        ProyectoDTO dtoNuevo = new ProyectoDTO();
        dtoNuevo.setTitulo("Mesa de Madera");
        dtoNuevo.setAnio(2024);
        dtoNuevo.setCategorias(new ArrayList<>());

        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyectoExistente));
        when(proyectoRepository.save(any())).thenReturn(proyectoExistente);

        service.actualizar(1L, dtoNuevo);

        verify(proyectoRepository).save(argThat(p -> "mesa-de-madera".equals(p.getSlug())));
    }

    @Test
    @DisplayName("actualizar conserva el slug cuando el título no cambia")
    void actualizar_cuandoTituloNoCambia_conservaSlug() {
        ProyectoDTO mismoTitulo = new ProyectoDTO();
        mismoTitulo.setTitulo("Silla Ergonómica");
        mismoTitulo.setAnio(2024);
        mismoTitulo.setCategorias(new ArrayList<>());

        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyectoExistente));
        when(proyectoRepository.save(any())).thenReturn(proyectoExistente);

        service.actualizar(1L, mismoTitulo);

        verify(proyectoRepository).save(argThat(p -> "silla-ergonomica".equals(p.getSlug())));
    }

    // ── eliminarLogico ────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarLogico invierte el flag activo")
    void eliminarLogico_cuandoEstaActivo_setaActivoFalse() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyectoExistente));

        service.eliminarLogico(1L);

        assertThat(proyectoExistente.getActivo()).isFalse();
        verify(proyectoRepository).save(proyectoExistente);
    }

    @Test
    @DisplayName("eliminarLogico lanza ResourceNotFoundException cuando no existe")
    void eliminarLogico_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarLogico(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── eliminarFisico ────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminarFisico borra el proyecto de la BD")
    void eliminarFisico_cuandoExiste_eliminaDeLaBD() {
        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyectoExistente));

        service.eliminarFisico(1L);

        verify(proyectoRepository).delete(proyectoExistente);
    }

    @Test
    @DisplayName("eliminarFisico llama a S3 por cada imagen con URL")
    void eliminarFisico_conImagenes_borraArchivosDeS3() {
        ImagenProyecto img = new ImagenProyecto();
        img.setUrlImagen("https://cdn.example.com/foto.jpg");
        proyectoExistente.setImagenes(List.of(img));

        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyectoExistente));

        service.eliminarFisico(1L);

        verify(s3Service).deleteFile("foto.jpg");
    }

    @Test
    @DisplayName("eliminarFisico lanza ResourceNotFoundException cuando no existe")
    void eliminarFisico_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarFisico(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── reordenarLote ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("reordenarLote asigna orden correcto a cada proyecto")
    void reordenarLote_asignaOrdenCorrecto() {
        Proyecto p2 = new Proyecto();
        p2.setIdProyecto(2L);

        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyectoExistente));
        when(proyectoRepository.findById(2L)).thenReturn(Optional.ofNullable(p2));

        service.reordenarLote(List.of(1L, 2L));

        assertThat(proyectoExistente.getOrden()).isEqualTo(1);
        assertThat(p2.getOrden()).isEqualTo(2);
    }
}
