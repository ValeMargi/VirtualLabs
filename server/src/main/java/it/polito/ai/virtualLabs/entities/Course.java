package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"professors", "students"})
public class Course {
    @Id
    private String name;
    private String acronym;
    private int min, max; //about teams
    private boolean enabled;

    //ModelVM
    private int maxVcpu, diskSpace, ram, runningInstances, totInstances;

    @ManyToMany(mappedBy = "courses")
    List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

   // @ToString.Exclude
    //@HashCodeExclude
    @ManyToMany(mappedBy = "courses")
    List<Professor> professors = new ArrayList<>();

    @OneToMany(mappedBy = "courseAssignment")
    private List<Assignment> assignments = new ArrayList<>();

   @OneToMany(mappedBy = "course")
   private List<VM> vms = new ArrayList<>();

    @OneToOne
    @JoinColumn(name="image_id")
    PhotoModelVM photoModelVM;

    public boolean addStudent(Student s) {
        if (!students.contains(s)) {
            students.add(s);
           s.setCourses(this);
            return true;
        }
        return false;
    }

    public boolean removeStudent(Student s) {
        if (students.contains(s)) {
            students.remove(s);
            s.removeCourses(this);
            return true;
        }
        return false;
    }

    public boolean removeTeam(Team t) {
        if (teams.contains(t)) {
            teams.remove(t);
            t.removeCourse(this);
            return true;
        }
        return false;
    }

    public void setProfessor(Professor  p){
        if(p!=null && !professors.contains(p)) {
            professors.add(p);
            p.setCourses(this);
        }
    }

    public void removeProfessor(Professor p){
        if(p!=null && professors.contains(p)){
            professors.remove(p);
           p.removeCourse(this);
        }
    }

    public void addAssigment(Assignment a){
        if(a!=null && !assignments.contains(a))
        {
            assignments.add(a);
            a.setCourseAssignment(this);
        }
    }

    public void setPhotoModelVM(PhotoModelVM p){
        if(p!=null && photoModelVM!=p){
            photoModelVM=p;
            p.setPhotoModelVM(this);
        }
    }
    public  void removeVM(VM vm){
        if(vm!=null && vms.contains(vm)){
            vms.remove(vm);
        }
    }

    public  void addVM(VM vm){
        if(vm!=null && !vms.contains(vm)){
            vms.add(vm);
        }
    }





}
