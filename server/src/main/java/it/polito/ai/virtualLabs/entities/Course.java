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

    //ModelVM
    private int maxVcpu, diskSpace, ram, runningInstances, totInstances;

    @ManyToMany(mappedBy = "courses")
    List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Team> teams = new ArrayList<>();

    @ManyToMany //(fetch = FetchType.EAGER) default
    @JoinColumn(name="professor_id")
    List<Professor> professors = new ArrayList<>();

    @OneToMany(mappedBy = "courseAssignment")
    private List<Assignment> assignments = new ArrayList<>();

   /* @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="modelVm_id")
    ModelVM modelVM;

    */
   @OneToMany(mappedBy = "vm")
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


    /*Gestire rimozione professore dal corso*/
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
