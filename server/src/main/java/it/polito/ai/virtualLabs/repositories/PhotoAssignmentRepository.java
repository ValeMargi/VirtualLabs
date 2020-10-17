package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PhotoAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoAssignmentRepository extends JpaRepository<PhotoAssignment, Long> {

}