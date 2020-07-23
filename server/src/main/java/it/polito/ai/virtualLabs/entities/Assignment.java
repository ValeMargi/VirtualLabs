package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Assignment {
    @Id
    private  String id;
    private Date release, expiration;

    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="course_id")
    Course courseAssignment;

    @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    Image imageAssignment;


    @OneToMany(mappedBy = "homeworks")
    private List<Homework> homeworks= new ArrayList<>();

    public void setCourseAssigment(Course c){
        if(c!=null && courseAssignment!=c ) {
            courseAssignment = c;
            c.getAssignments().add(this);
        }
    }

    public void addHomework(Homework h){
        if(h!=null && !homeworks.contains(h))
        {
            homeworks.add(h);
            h.setAssignment(this);
        }
    }

    public void setImageAssignment(Image i){
        if(i != null && imageAssignment!=i){
            imageAssignment = i;
            i.setAssignment(this);
        }
    }

}
