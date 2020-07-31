package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;

@Data
@Entity
public class Professor {
    @Id
    private String id;
    private String name, firstName;
    @Email
    private String email;

    @ManyToMany(mappedBy = "professors")
    private List<Course> courses = new ArrayList<>();

    @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    AvatarProfessor photoProfessor;

    @OneToOne(mappedBy = "professor")
    private UserDAO userDAO;


    public void removeCourse(Course c){
        if(c!=null && courses.contains(c)){
            courses.remove(c);
            c.getProfessors().remove(this);
        }
    }

    public  void setPhotoProfessor(AvatarProfessor a){
        if( a!= null){
            photoProfessor = a;
            a.setProfessor(this);
        }
    }

    public void setCourses(Course c){
        if(c!=null && !courses.contains(c)){
            courses.add(c);
            c.setProfessor(this);
        }
    }

    public void removeCourses(Course c){
        if(c!=null && courses.contains(c)){
            courses.remove(c);
            c.removeProfessor(this);
        }
    }
}
