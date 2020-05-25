package it.polito.ai.virtualalbs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Professor {
    @Id
    private String id;
    private String name, firstName, email, photoId;

    @ManyToMany(mappedBy = "professors")
    private List<Course> courses = new ArrayList<>();

    @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    Image photoProfessor;
}
