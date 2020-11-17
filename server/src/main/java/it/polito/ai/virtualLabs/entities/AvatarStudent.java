package it.polito.ai.virtualLabs.entities;
import lombok.*;
import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude="student", callSuper = false)
@ToString(exclude = {"student"})
@Entity(name = "AvatarStudent")
public class AvatarStudent  extends Image{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    @OneToOne(mappedBy = "photoStudent")
    private Student student;

    public  void setStudent(Student s){
        if(s!=null && s!=student){
            student = s;
            s.setPhotoStudent(this);
        }
    }

    public  AvatarStudent( Image image){
        super(image);
    }
}