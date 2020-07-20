package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDAO, String> {
    UserDAO findByEmail(String email);
}