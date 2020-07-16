package it.polito.ai.virtualalbs.repositories;

import it.polito.ai.virtualalbs.entities.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, String> {
}
