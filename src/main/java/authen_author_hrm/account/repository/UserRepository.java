package authen_author_hrm.account.repository;

import authen_author_hrm.account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository < User, Long> {
    Optional <User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Query(value = "select users.username from users where id = :userId",nativeQuery = true)
    String findUsername(Long userId);

    @Override
    boolean existsById(Long id);
}
