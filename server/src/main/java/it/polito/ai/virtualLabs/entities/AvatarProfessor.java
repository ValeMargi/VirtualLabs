package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "AvatarProfessor")
public class AvatarProfessor extends Image{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

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