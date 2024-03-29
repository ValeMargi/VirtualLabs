package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.AvatarStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvatarStudentRepository extends JpaRepository<AvatarStudent, Long> {
}