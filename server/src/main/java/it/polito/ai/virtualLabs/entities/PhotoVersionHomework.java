package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity(name = "PhotoVersionHomework")
public class PhotoVersionHomework extends Image{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;


    @ManyToOne
    @JoinColumn(name="homework_id")
    private Homework homework;

    private String timestamp;


    public void setPhotoVersionHomework(Homework h){
        if( h!=null && homework!=h){
            homework=h;
            h.setPhotoVersionHomework(this);
        }
    }

    public  PhotoVersionHomework( Image image){
        super(image);
    }
}
