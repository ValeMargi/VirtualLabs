package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.VM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VMRepository extends JpaRepository<VM, Long> {
}
