package it.polito.ai.virtualalbs.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Data
@Entity
public class Image {
    @Id
    private String id;
    private Timestamp timestamp;

    @OneToOne(mappedBy = "photoStudent")
    private Student student;
    @OneToOne(mappedBy = "photoProfessor")
    private  Professor professor;
    @OneToOne(mappedBy = "photoAssignment")
    private Assignment assignment;

    @OneToMany(mappedBy = "photoHomework")
    private List<Homework> homeworks = new ArrayList<>();
}
