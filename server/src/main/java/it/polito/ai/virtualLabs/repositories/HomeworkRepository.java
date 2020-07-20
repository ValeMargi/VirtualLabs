package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, String> {
}
