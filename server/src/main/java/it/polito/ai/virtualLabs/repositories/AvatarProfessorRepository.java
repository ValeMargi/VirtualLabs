package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.AvatarProfessor;
import it.polito.ai.virtualLabs.entities.AvatarStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarProfessorRepository extends JpaRepository<AvatarProfessor, String> {
    Optional<AvatarProfessor> findByName(String name);
}
