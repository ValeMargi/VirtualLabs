package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "PhotoAssignment")
public class PhotoAssignment extends  Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    @OneToOne(mappedBy = "photoAssignment")
    private Assignment assignment;

    private String timestamp;

    public void setAssignment(Assignment a){
        if( a!=null && assignment != a){
            assignment =a;
            a.setPhotoAssignment(this);
        }
    }

    public  PhotoAssignment( Image image){
        super(image);
    }
}