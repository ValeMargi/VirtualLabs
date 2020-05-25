package it.polito.ai.virtualalbs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {
    @Id
    private String acronym;
    private String name;
    private int min, max;
    private boolean enabled;

    @ManyToMany(mappedBy = "courses")
    List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

    @ManyToMany //(fetch = FetchType.EAGER) default
    @JoinColumn(name="professor_id")
    List<Professor> professors = new ArrayList<>();

    @OneToMany(mappedBy = "courseAssignment")
    private List<Assignment> assignments = new ArrayList<>();
}
