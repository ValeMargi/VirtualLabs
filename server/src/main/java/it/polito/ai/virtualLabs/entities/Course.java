package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

    private String acronym;
    @Id
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

    @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="modelVm_id")
    ModelVM modelVM;


    public boolean addStudent(Student s) {
        if (!students.contains(s)) {
            students.add(s);
            s.getCourses().add(this);
            return true;
        }
        return false;
    }

    public boolean removeStudent(Student s) {
        if (students.contains(s)) {
            students.remove(s);
            s.getCourses().remove(this);
            return true;
        }
        return false;
    }


    /*Gestire rimozione professore dal corso*/
    public void setProfessor(Professor  p){
       /* if( p == null){
            if(!professors.isEmpty())
                professor.getCourses().remove(this);
            professor = null;
        }else{*/
        if(!professors.contains(p)) {
            professors.add(p);
            p.getCourses().add(this);
        }
    }

    public void removeProfessor(Professor p){
        if(p!=null && professors.contains(p)){
            professors.remove(p);
            p.getCourses().remove(this);
        }
    }

    public void addAssigment(Assignment a){
        if(a!=null && !assignments.contains(a))
        {
            assignments.add(a);
            a.setCourseAssignment(this);
        }
    }





}