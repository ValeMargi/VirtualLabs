package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Entity
@Data
public class Token {
    @Id
    private String id, courseId;
    private Long teamId;
    private Timestamp expiryDate;
    private Boolean status; //false: in attesa, true:accettato

    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="student_id")
    Student  student;

    public void setStudentToken(Student s){
        if(s!=null && student!=s) {
            student = s;
            s.setTokenForStudent(this);
        }
    }

}
