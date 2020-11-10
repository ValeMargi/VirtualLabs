package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.ToString;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;

@Data
@ToString(exclude = {"photoProfessor", "courses"})
//@EqualsAndHashCode(exclude="courses")
@Entity
public class Professor {
    @Id
    private String id;
    private String name, firstName;
    @Email
    private String email;

    //@ToString.Exclude
  //  @HashCodeExclude
    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="professor_course",
            joinColumns = @JoinColumn(name="professor_id"),
            inverseJoinColumns = @JoinColumn(name="course_name"))
    private List<Course> courses = new ArrayList<>();

    //@HashCodeExclude
   // @ToString.Exclude
    @OneToOne
    @JoinColumn(name="image_id")
    AvatarProfessor photoProfessor;

    @OneToOne(mappedBy = "professor")
    private UserDAO userDAO;

    public  void setPhotoProfessor(AvatarProfessor a){
        if( a!= null && a!=photoProfessor){
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
    public void removeCourse(Course c){
        if(c!=null && courses.contains(c)){
            courses.remove(c);
            c.removeProfessor(this);
        }
    }

}
