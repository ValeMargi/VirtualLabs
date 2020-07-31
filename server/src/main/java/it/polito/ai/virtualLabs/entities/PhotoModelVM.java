package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
@Data
@NoArgsConstructor
@Entity(name = "PhotoModelVM")
public class PhotoModelVM extends Image {

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
