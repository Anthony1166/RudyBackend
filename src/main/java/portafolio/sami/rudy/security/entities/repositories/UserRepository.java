package portafolio.sami.rudy.security.entities.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import portafolio.sami.rudy.security.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String userName);
    boolean existsByUsername(String userName);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
