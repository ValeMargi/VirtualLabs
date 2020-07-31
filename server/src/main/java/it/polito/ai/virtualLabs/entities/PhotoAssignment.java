package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity(name = "PhotoAssignment")
public class PhotoAssignment extends  Image {

    /*@OneToOne(mappedBy = "photoAssignment")
    private Assignment assignment;

    private Timestamp timestamp;

    public void setAssignment(Assignment a){
        if( a!=null && assignment != a){
            assignment =a;
            a.setPhotoAssignment(this);
        }
    }
*/
    public  PhotoAssignment( Image image){
        super(image);
    }
}
