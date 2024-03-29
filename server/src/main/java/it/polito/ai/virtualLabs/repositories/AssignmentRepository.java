package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository  extends JpaRepository<Assignment, Long> {
}
