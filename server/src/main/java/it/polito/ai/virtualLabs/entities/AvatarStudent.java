package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;


@Data
@NoArgsConstructor
@Entity(name = "AvatarStudent")
public class AvatarStudent extends Image {

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
