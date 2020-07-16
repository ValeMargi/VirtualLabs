package it.polito.ai.virtualalbs.repositories;

import it.polito.ai.virtualalbs.entities.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomewrokRepository  extends JpaRepository<Homework, String> {
}
