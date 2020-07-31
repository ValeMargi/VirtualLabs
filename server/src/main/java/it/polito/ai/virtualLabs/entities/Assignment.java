package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Assignment extends Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    private Date releaseDate, expiration;

    private Timestamp timestamp;


    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="course_id")
    Course courseAssignment;

    /*@OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    PhotoAssignment photoAssignment;
    */

    @OneToMany(mappedBy = "assignment")
    private List<Homework> homeworks= new ArrayList<>();

    public  Assignment( Image image){
        super(image);
    }

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

   /* public void setPhotoAssignment(PhotoAssignment p){
        if(p != null && photoAssignment!=p){
            photoAssignment = p;
            p.setAssignment(this);
        }
    }*/



}
