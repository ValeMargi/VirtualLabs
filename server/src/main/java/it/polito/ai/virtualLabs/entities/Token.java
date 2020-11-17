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
    private String id;
    private String courseId;
    private Long teamId;
    private Timestamp expiryDate;
    /**
     * la stringa status può avere 3 valori:
     *  - "accepted": se lo studente ha accettato l'invito al team
     *  - "pending": se lo studente non ha ancora dato una risposta all'invito al team
     *  - "rejected": se lo studente ha rifiutato l'invito al team
     */
    private String status;

    @ManyToOne
    @JoinColumn(name="student_id")
    Student  student;

    public void setStudentToken(Student s){
        if(s!=null && student!=s) {
            student = s;
            s.setTokenForStudent(this);
        }
    }
}
