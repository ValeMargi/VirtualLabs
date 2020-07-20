package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;

@Data
@Entity
public class Professor {
    @Id
    private String id;
    private String name, firstName, photoId;
    @Email
    private String email;

    @ManyToMany(mappedBy = "professors")
    private List<Course> courses = new ArrayList<>();

    @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    Image photoProfessor;

    @OneToOne(mappedBy = "professorEmail")
    private UserDAO userDAO;
}
