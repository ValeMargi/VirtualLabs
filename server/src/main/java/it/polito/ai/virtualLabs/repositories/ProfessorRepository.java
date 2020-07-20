package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, String> {
}
