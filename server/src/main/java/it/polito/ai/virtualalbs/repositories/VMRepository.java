package it.polito.ai.virtualalbs.repositories;

import it.polito.ai.virtualalbs.entities.VM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VMRepository extends JpaRepository<VM, String> {
}
