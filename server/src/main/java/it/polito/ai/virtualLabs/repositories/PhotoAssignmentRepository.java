package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.AvatarProfessor;
import it.polito.ai.virtualLabs.entities.PhotoAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhotoAssignmentRepository extends JpaRepository<PhotoAssignment, String> {
    Optional<PhotoAssignment> findByName(String name);
}