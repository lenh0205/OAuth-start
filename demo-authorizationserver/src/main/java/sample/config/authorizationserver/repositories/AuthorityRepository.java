package sample.config.authorizationserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sample.config.authorizationserver.entities.Authority;
import sample.config.authorizationserver.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<List<Authority>> findByUsername(String username);
}
