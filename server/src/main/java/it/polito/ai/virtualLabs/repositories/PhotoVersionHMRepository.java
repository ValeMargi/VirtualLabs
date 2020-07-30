package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PhotoVersionHomework;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoVersionHMRepository  extends JpaRepository<PhotoVersionHomework, String> {
}

