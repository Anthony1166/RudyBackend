package portafolio.sami.rudy.security.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import portafolio.sami.rudy.security.entities.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
    Optional<Role> findByName(String name);
}
