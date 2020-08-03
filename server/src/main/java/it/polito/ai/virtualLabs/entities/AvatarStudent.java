package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@NoArgsConstructor
@Entity(name = "AvatarStudent")
public class AvatarStudent extends Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    @OneToOne(mappedBy = "photoStudent")
    private Student student;

    public  void setStudent(Student s){
        if(s!=null){
            student = s;
            s.setPhotoStudent(this);
        }
    }

    public  AvatarStudent( Image image){
        super(image);

    }
}