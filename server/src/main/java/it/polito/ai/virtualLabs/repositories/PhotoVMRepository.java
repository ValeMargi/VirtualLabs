package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PhotoVM;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PhotoVMRepository  extends JpaRepository<PhotoVM, String> {
}


