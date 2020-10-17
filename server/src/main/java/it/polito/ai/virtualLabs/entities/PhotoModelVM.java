package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "PhotoModelVM")
public class PhotoModelVM extends Image {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    @OneToOne(mappedBy = "photoModelVM")
    private Course course;

    public  PhotoModelVM( Image image){
        super(image);
    }

    public  void setPhotoModelVM(Course c){
        if(c!=null && course!=c){
            course=c;
            c.setPhotoModelVM(this);
        }
    }
}
