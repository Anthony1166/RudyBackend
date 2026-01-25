package portafolio.sami.rudy.services;

import portafolio.sami.rudy.entities.Proyecto;

import java.util.List;

public interface ProyectoServices {
    List<Proyecto> findAll();
    Proyecto buscarProyectoID (Long id);
    Proyecto registrarProyecto(Proyecto proyecto);
    Proyecto actualizarProyecto(Long id, Proyecto proyectoActualizado);
    void eliminarProyecto(Long id);
    List<Proyecto> filtrarPorCategoria(Long categoriaId);
    List<Proyecto> filtrarPorAnio(Integer anio);

}
