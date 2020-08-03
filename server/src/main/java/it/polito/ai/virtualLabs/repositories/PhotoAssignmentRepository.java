package it.polito.ai.virtualLabs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoAssignmentRepository extends JpaRepository<PhotoAssignment, String> {
    Optional<PhotoAssignment> findByName(String name);
}