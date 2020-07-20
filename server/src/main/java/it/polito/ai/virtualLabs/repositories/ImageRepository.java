package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository  extends JpaRepository<Image, String> {
    Optional<Image> findByName(String name);
}
