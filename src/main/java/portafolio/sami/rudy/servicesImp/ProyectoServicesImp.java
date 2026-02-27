package portafolio.sami.rudy.servicesImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import portafolio.sami.rudy.entities.Proyecto;
import portafolio.sami.rudy.repositories.ProyectoRepository;
import portafolio.sami.rudy.services.ProyectoServices;

import java.util.List;

@Service
public class ProyectoServicesImp implements ProyectoServices {
    @Autowired
    private ProyectoRepository proyectoRepository;

    @Override
    public List<Proyecto> findAll() {
        return proyectoRepository.findAll();
    }

    @Override
    public Proyecto buscarProyectoID(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
    }

    @Override
    public Proyecto registrarProyecto(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    @Override
    public Proyecto actualizarProyecto(Long id, Proyecto proyectoActualizado) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));

        proyecto.setTitulo(proyectoActualizado.getTitulo());
        proyecto.setSubtitulo(proyectoActualizado.getSubtitulo()); // Campo añadido
        proyecto.setDescripcion(proyectoActualizado.getDescripcion());
        proyecto.setAnio(proyectoActualizado.getAnio());
        
        // Actualizamos la lista de categorías
        proyecto.setCategorias(proyectoActualizado.getCategorias());
        
        return proyectoRepository.save(proyecto);
    }

    @Override
    public void eliminarProyecto(Long id) {
        proyectoRepository.deleteById(id);
    }
    
    @Override
    public List<Proyecto> filtrarPorCategoria(Long categoriaId) {
        // Usamos el nuevo método del repositorio (en plural)
        return proyectoRepository.findByCategoriasIdCategoria(categoriaId);
    }

    @Override
    public List<Proyecto> filtrarPorAnio(Integer anio) {
        return proyectoRepository.findByAnio(anio);
    }
}
