package portafolio.sami.rudy.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import portafolio.sami.rudy.dto.ProyectoDTO;
import portafolio.sami.rudy.entities.Proyecto;
import portafolio.sami.rudy.services.ProyectoServices;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/sami")
public class ProyectoController {
    @Autowired
    private ProyectoServices proyectoServices;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/proyectos")
    public List<ProyectoDTO> findAll() {
        List<Proyecto> proyectos = proyectoServices.findAll();
        List<ProyectoDTO> proyectoDTO = Arrays.asList(modelMapper.map(proyectos, ProyectoDTO[].class));
        return proyectoDTO;
    }

    @GetMapping("/proyecto/{id}")
    public ProyectoDTO buscarProyectoID(@PathVariable Long id) {
        Proyecto proyecto = proyectoServices.buscarProyectoID(id);
        ProyectoDTO proyectoDTO = modelMapper.map(proyecto, ProyectoDTO.class);
        return proyectoDTO;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/proyecto")
    public ProyectoDTO registrarProyecto(@RequestBody ProyectoDTO proyectoDTO) {
        Proyecto proyecto = modelMapper.map(proyectoDTO, Proyecto.class);
        proyecto = proyectoServices.registrarProyecto(proyecto);
        proyectoDTO = modelMapper.map(proyecto, ProyectoDTO.class);
        return proyectoDTO;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/proyecto/{id}")
    public ProyectoDTO actualizarProyecto(@PathVariable Long id, @RequestBody ProyectoDTO proyectoDTO) {
        Proyecto proyecto = modelMapper.map(proyectoDTO, Proyecto.class);
        proyecto = proyectoServices.actualizarProyecto(id, proyecto);
        proyectoDTO = modelMapper.map(proyecto, ProyectoDTO.class);
        return proyectoDTO;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/proyecto/{id}")
    public void eliminarProyecto(@PathVariable Long id) { proyectoServices.eliminarProyecto(id); }

    @GetMapping("/proyectoByCat/{categoriaId}")
    public List<ProyectoDTO> filtrarPorCategoria(@PathVariable Long categoriaId) {
        List<Proyecto> proyectos = proyectoServices.filtrarPorCategoria(categoriaId);
        return proyectos.stream().map(p -> modelMapper.map(p, ProyectoDTO.class)).toList();
    }
    @GetMapping("/proyectosAnio/{anio}")
    public List<ProyectoDTO> listarPorAnio(@PathVariable Integer anio) {
        List<Proyecto> proyectos = proyectoServices.filtrarPorAnio(anio);
        return proyectos.stream()
                .map(p -> modelMapper.map(p, ProyectoDTO.class))
                .toList();
    }
}