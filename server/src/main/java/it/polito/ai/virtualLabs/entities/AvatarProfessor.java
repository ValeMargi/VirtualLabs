package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Data
@NoArgsConstructor
@Entity(name = "AvatarProfessor")
public class AvatarProfessor extends Image{
    @OneToOne(mappedBy = "photoProfessor")
    private Professor professor;

    public  void setProfessor(Professor p){
        if(p!=null){
            professor = p;
            p.setPhotoProfessor(this);
        }
    }
    public  AvatarProfessor( Image image){
        super(image);

    }

}
