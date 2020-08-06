package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.TokenRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.security.Timestamp;
import java.util.List;

public interface TokenRegistrationRepository extends JpaRepository<TokenRegistration, String> {
    List<TokenRegistration> findAllByExpiryDateBefore(Timestamp t);


}

