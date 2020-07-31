package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity(name = "PhotoCorrection")
public class PhotoCorrection extends  Image{

    @ManyToOne
    @JoinColumn(name="homework_id")
    private Homework homework;

    private Timestamp timestamp;


    public void setHomework(Homework h){
        if( h!=null && homework!=h){
            homework=h;
           h.setPhotoCorrection(this);
        }
    }

    public  PhotoCorrection( Image image){
        super(image);
    }
}
