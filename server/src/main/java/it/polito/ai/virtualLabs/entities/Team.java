package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Team {
    @Id
    @GeneratedValue
    private Long id;
    private  String name;
    private int status, maxVpcu, diskSpace, ram, runningInstances, totInstances;


    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "team_student",
            joinColumns = @JoinColumn(name="team_id"),
            inverseJoinColumns = @JoinColumn(name="student_id"))
    List<Student> members = new ArrayList<>();

    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="course_id")
    Course course;


    public void addStudentIntoTeam(Student s) {
        if (!members.contains(s)) {
            members.add(s);
            s.getTeams().add(this);
        }

    }
    public void removeStudentFromTeam(Student s){
        if( members.contains(s)){
            members.remove(s);
            s.getTeams().remove(this);
        }
    }

}
