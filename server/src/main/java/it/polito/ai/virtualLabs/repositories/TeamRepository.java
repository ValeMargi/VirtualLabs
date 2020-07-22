package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Student;
import it.polito.ai.virtualLabs.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository  extends JpaRepository<Team, Long> {
    @Query("SELECT t.members FROM Team  t INNER JOIN t.course c WHERE c.name=:courseName AND t.name=:nameTeam")
    List<Student> getStudentsTeamCourse(String courseName, String nameTeam);

}

