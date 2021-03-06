package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PhotoModelVM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoModelVMRepository extends JpaRepository<PhotoModelVM, String> {
}
