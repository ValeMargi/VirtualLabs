package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.TokenRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRegistrationRepository extends JpaRepository<TokenRegistration, String> {
}

