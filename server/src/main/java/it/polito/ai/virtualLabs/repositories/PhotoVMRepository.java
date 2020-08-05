package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PhotoVM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoVMRepository  extends JpaRepository<PhotoVM, Long> {
}


