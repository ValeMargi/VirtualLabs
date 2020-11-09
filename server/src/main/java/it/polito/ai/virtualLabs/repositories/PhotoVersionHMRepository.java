package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Homework;
import it.polito.ai.virtualLabs.entities.PhotoVersionHomework;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PhotoVersionHMRepository  extends JpaRepository<PhotoVersionHomework, Long> {
    List<PhotoVersionHomework> findAllByHomework(Homework homework);
}

