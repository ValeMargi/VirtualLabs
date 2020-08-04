package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Assignment {

   // @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @GeneratedValue(generator="optimized-sequence")
    private  Long id;
    private  String nameAssignment;
    private Date releaseDate, expiration;




    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="course_id")
    Course courseAssignment;

    @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    PhotoAssignment photoAssignment;


    @OneToMany(mappedBy = "assignment")
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
