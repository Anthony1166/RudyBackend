package portafolio.sami.rudy.servicesImp.proy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import portafolio.sami.rudy.dto.proy.ImagenProyectoDTO;
import portafolio.sami.rudy.entities.proy.ImagenProyecto;
import portafolio.sami.rudy.entities.proy.Proyecto;
import portafolio.sami.rudy.exceptions.ResourceNotFoundException;
import portafolio.sami.rudy.repositories.proy.ImagenProyectoRepository;
import portafolio.sami.rudy.repositories.proy.ProyectoRepository;
import portafolio.sami.rudy.servicesImp.S3Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImagenProyectoServicesImp")
class ImagenProyectoServicesImpTest {

    @Mock private ImagenProyectoRepository imagenRepository;
    @Mock private ProyectoRepository proyectoRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private S3Service s3Service;

    @InjectMocks
    private ImagenProyectoServicesImp service;

    private Proyecto proyecto;
    private ImagenProyecto imagenExistente;
    private ImagenProyectoDTO imagenDTO;

    @BeforeEach
    void setUp() {
        proyecto = new Proyecto();
        proyecto.setIdProyecto(1L);
        proyecto.setTitulo("Silla");

        imagenExistente = new ImagenProyecto();
        imagenExistente.setId(10L);
        imagenExistente.setUrlImagen("https://cdn.example.com/silla.jpg");
        imagenExistente.setEsPortada(false);
        imagenExistente.setOrden(1);
        imagenExistente.setProyecto(proyecto);

        imagenDTO = new ImagenProyectoDTO();
        imagenDTO.setUrlImagen("https://cdn.example.com/silla.jpg");
        imagenDTO.setEsPortada(false);
    }

    // ── listarPorProyecto ─────────────────────────────────────────────────────

    @Test
    @DisplayName("listarPorProyecto retorna lista mapeada")
    void listarPorProyecto_retornaListaMapeada() {
        when(imagenRepository.findByProyectoIdProyectoOrderByOrdenAsc(1L)).thenReturn(List.of(imagenExistente));
        when(modelMapper.map(imagenExistente, ImagenProyectoDTO.class)).thenReturn(imagenDTO);

        List<ImagenProyectoDTO> resultado = service.listarPorProyecto(1L);

        assertThat(resultado).hasSize(1);
    }

    // ── agregarAProyecto ──────────────────────────────────────────────────────

    @Test
    @DisplayName("agregarAProyecto lanza ResourceNotFoundException cuando el proyecto no existe")
    void agregarAProyecto_proyectoNoExiste_lanzaResourceNotFoundException() {
        when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.agregarAProyecto(99L, imagenDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("agregarAProyecto asigna el proyecto y calcula el orden")
    void agregarAProyecto_asignaProyectoYOrden() {
        ImagenProyecto nuevaImagen = new ImagenProyecto();

        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyecto));
        when(modelMapper.map(imagenDTO, ImagenProyecto.class)).thenReturn(nuevaImagen);
        when(imagenRepository.obtenerMaximoOrdenPorProyecto(1L)).thenReturn(5);
        when(imagenRepository.save(any())).thenReturn(nuevaImagen);
        when(modelMapper.map(nuevaImagen, ImagenProyectoDTO.class)).thenReturn(imagenDTO);

        service.agregarAProyecto(1L, imagenDTO);

        assertThat(nuevaImagen.getProyecto()).isEqualTo(proyecto);
        assertThat(nuevaImagen.getOrden()).isEqualTo(6);
    }

    @Test
    @DisplayName("agregarAProyecto destituye la portada anterior cuando la nueva es portada")
    void agregarAProyecto_nuevaEsPortada_destituyePortadaAnterior() {
        imagenDTO.setEsPortada(true);

        ImagenProyecto portadaAnterior = new ImagenProyecto();
        portadaAnterior.setEsPortada(true);

        ImagenProyecto nuevaImagen = new ImagenProyecto();
        nuevaImagen.setEsPortada(true);

        when(proyectoRepository.findById(1L)).thenReturn(Optional.ofNullable(proyecto));
        when(modelMapper.map(imagenDTO, ImagenProyecto.class)).thenReturn(nuevaImagen);
        when(imagenRepository.findByProyectoIdProyectoAndEsPortadaTrue(1L)).thenReturn(Optional.of(portadaAnterior));
        when(imagenRepository.obtenerMaximoOrdenPorProyecto(1L)).thenReturn(1);
        when(imagenRepository.save(any())).thenReturn(nuevaImagen);
        when(modelMapper.map(nuevaImagen, ImagenProyectoDTO.class)).thenReturn(imagenDTO);

        service.agregarAProyecto(1L, imagenDTO);

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
    @DisplayName("actualizar actualiza url y textoAlternativo")
    void actualizar_actualizaCampos() {
        imagenDTO.setTextoAlternativo("Vista lateral");
        imagenDTO.setUrlImagen("https://cdn.example.com/nueva.jpg");

        when(imagenRepository.findById(10L)).thenReturn(Optional.ofNullable(imagenExistente));
        when(imagenRepository.save(any())).thenReturn(imagenExistente);
        when(modelMapper.map(imagenExistente, ImagenProyectoDTO.class)).thenReturn(imagenDTO);

        service.actualizar(10L, imagenDTO);

        assertThat(imagenExistente.getTextoAlternativo()).isEqualTo("Vista lateral");
        assertThat(imagenExistente.getUrlImagen()).isEqualTo("https://cdn.example.com/nueva.jpg");
    }

    // ── eliminar ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar borra la imagen de la BD y el archivo de S3")
    void eliminar_cuandoExiste_eliminaDeLaBDYS3() {
        when(imagenRepository.findById(10L)).thenReturn(Optional.ofNullable(imagenExistente));

        service.eliminar(10L);

        verify(s3Service).deleteFile("silla.jpg");
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
        ImagenProyecto img2 = new ImagenProyecto();
        img2.setId(20L);

        when(imagenRepository.findById(10L)).thenReturn(Optional.ofNullable(imagenExistente));
        when(imagenRepository.findById(20L)).thenReturn(Optional.ofNullable(img2));

        service.reordenarLote(List.of(10L, 20L));

        assertThat(imagenExistente.getOrden()).isEqualTo(1);
        assertThat(img2.getOrden()).isEqualTo(2);
    }
}
