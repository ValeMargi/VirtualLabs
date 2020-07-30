package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PhotoCorrection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoCorrectionRepository extends JpaRepository<PhotoCorrection, String> {
}
