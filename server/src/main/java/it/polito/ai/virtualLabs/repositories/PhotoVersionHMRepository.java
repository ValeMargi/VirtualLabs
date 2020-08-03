package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PhotoVersionHomework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoVersionHMRepository  extends JpaRepository<PhotoVersionHomework, Long> {
}

