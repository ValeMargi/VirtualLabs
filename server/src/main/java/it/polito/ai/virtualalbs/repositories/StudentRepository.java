package it.polito.ai.virtualalbs.repositories;

import it.polito.ai.virtualalbs.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
}

