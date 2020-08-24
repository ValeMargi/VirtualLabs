package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.AvatarStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AvatarStudentRepository extends JpaRepository<AvatarStudent, Long> {
    Optional<AvatarStudent> findByNameFile(String nameFile);
}