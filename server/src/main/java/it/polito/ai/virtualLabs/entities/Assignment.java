package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Assignment {
    @Id
    private  String id;
    private String content;
    private Date release, expiration;

    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="course_id")
    Course courseAssignment;

    @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    Image photoAssignment;
}
