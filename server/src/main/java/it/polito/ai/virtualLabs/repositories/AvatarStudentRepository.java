package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.AvatarStudent;
import it.polito.ai.virtualLabs.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarStudentRepository extends JpaRepository<AvatarStudent, String> {
    Optional<AvatarStudent> findByName(String name);
}
