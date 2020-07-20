package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.ModelVM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelVMRepository extends JpaRepository<ModelVM, String> {
}
