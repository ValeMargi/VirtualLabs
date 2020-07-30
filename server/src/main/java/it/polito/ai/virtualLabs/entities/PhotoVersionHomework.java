package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Data
@Entity(name = "PhotoVersionHomework")
public class PhotoVersionHomework extends Image{

    @ManyToOne
    @JoinColumn(name="homework_id")
    private Homework homework;

    private Timestamp timestamp;


    public void setPhotoVersionHomework(Homework h){
        if( h!=null && homework!=h){
            homework=h;
            h.getVersions().add(this);
        }
    }

    public  PhotoVersionHomework( Image image){
        super(image);
    }
}
