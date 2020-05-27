package it.polito.ai.virtualalbs.repositories;

import it.polito.ai.virtualalbs.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository  extends JpaRepository<Image, String> {
    Optional<Image> findByName(String name);
}
