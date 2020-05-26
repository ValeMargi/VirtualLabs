package it.polito.ai.virtualalbs.repositories;

import it.polito.ai.virtualalbs.entities.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDAO, String> {
    UserDAO findByEmail(String email);
}