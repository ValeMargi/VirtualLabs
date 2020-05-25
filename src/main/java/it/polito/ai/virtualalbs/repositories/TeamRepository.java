package it.polito.ai.virtualalbs.repositories;

import it.polito.ai.virtualalbs.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository  extends JpaRepository<Team, Long> {
}

