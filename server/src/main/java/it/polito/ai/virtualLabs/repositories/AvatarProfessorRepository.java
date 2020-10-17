package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.AvatarProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarProfessorRepository extends JpaRepository<AvatarProfessor, Long> {
}