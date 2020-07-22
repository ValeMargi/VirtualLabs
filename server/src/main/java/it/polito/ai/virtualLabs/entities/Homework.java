package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Data
@Entity
public class Homework {
    @Id
    private String id;
    private  String status;
    private Boolean permanent; //false può essere  ancora modificato
    private String grade;

    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="student_id")
    Student  student;

    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    Image  photoHomework;

    @ManyToOne
    @JoinColumn(name="assignment_id")
    Assignment assignment;

    public void setAssignment(Assignment a){
        if(a!=null) {
            assignment = a;
            a.addHomework(this);
        }
    }
}
