package it.polito.ai.virtualalbs.repositories;

import it.polito.ai.virtualalbs.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository  extends JpaRepository<Assignment, String> {
}
