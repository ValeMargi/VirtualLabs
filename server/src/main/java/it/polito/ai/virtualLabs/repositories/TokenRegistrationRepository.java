package it.polito.ai.virtualLabs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.security.Timestamp;
import java.util.List;

public interface TokenRegistration extends JpaRepository<TokenRegistration, String> {
    List<TokenRegistration> findAllByExpiryDateBefore(Timestamp t);

    List<TokenRegistration> findAllByTeamId(Long teamId);

}

