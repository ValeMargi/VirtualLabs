package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;

import javax.persistence.*;

@Data
@ToString(exclude = {"professor"})
@NoArgsConstructor
@EqualsAndHashCode(exclude="professor", callSuper = false)
@Entity(name = "AvatarProfessor")
public class AvatarProfessor extends Image{

    @Id @GeneratedValue(generator="optimized-sequence")
    private  Long id;


    @OneToOne(mappedBy = "photoProfessor")

    private Professor professor;

    public  void setProfessor(Professor p){
        if(p!=null  && professor!=p){
            professor = p;
            p.setPhotoProfessor(this);
        }
    }
    public  AvatarProfessor( Image image){
        super(image);

    }

}