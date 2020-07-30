package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.AvatarStudent;
import it.polito.ai.virtualLabs.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvatarStudentRepository extends JpaRepository<AvatarStudent, String> {
    Optional<AvatarStudent> findByName(String name);
}
