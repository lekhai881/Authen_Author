package authen_author_hrm.account.repository;

import authen_author_hrm.account.model.ERole;
import authen_author_hrm.account.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository < Role, Long> {
    Optional <Role> findByName(ERole name);
   Optional <Role> findByName(String name);

}