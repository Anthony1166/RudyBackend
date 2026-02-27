package portafolio.sami.rudy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import portafolio.sami.rudy.entities.ProcesoDiseno;

import java.util.List;

@Repository
public interface ProcesoDisenoRepository extends JpaRepository<ProcesoDiseno, Long> {

    // Método para buscar todos los procesos de un proyecto específico, ordenados por el campo 'orden'
    List<ProcesoDiseno> findByProyectoIdProyectoOrderByOrdenAsc(Long proyectoId);
}
