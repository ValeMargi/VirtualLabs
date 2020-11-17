package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Assignment {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    private  String assignmentName;
    private String releaseDate, expiration;
    private Boolean alreadyExpired;

    @ManyToOne
    @JoinColumn(name="course_id")
    Course courseAssignment;

    @OneToOne
    @JoinColumn(name="image_id")
    PhotoAssignment photoAssignment;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "assignment")
    private List<Homework> homeworks= new ArrayList<>();


    public void setCourseAssignment(Course c){
        if(c!=null && courseAssignment!=c ) {
            courseAssignment = c;
            c.addAssigment(this);
        }
    }

    public void addHomework(Homework h){
        if(h!=null && !homeworks.contains(h))
        {
            homeworks.add(h);
            h.setAssignment(this);
        }
    }

    public void setPhotoAssignment(PhotoAssignment p){
        if(p != null && photoAssignment!=p){
            photoAssignment = p;
            p.setAssignment(this);
        }
    }



}
