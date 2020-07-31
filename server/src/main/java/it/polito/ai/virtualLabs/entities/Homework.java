package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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

    @OneToMany(mappedBy = "homework")
    private List<PhotoVersionHomework> versions = new ArrayList<>();

    @OneToMany(mappedBy = "homework")
    private List<PhotoCorrection> corrections = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="assignment_id")
    Assignment assignment;

    public void setAssignment(Assignment a){
        if(a!=null) {
            assignment = a;
            a.addHomework(this);
        }
    }

    public void setPhotoVersionHomework(PhotoVersionHomework p){
        if( p!=null && !versions.contains(p))
        {
            versions.add(p);
            p.setPhotoVersionHomework(this);
        }
    }

    public void setPhotoCorrection(PhotoCorrection p){
        if(p!=null && !corrections.contains(p)){
            corrections.add(p);
            p.setHomework(this);
        }
    }

    public void setStudentForHomework(Student s){
        if(s!=null && student!=s) {
            student = s;
            s.setHomeworkForStudent(this);
        }
    }
}
