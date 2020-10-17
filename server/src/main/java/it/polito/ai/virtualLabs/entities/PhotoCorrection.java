package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "PhotoCorrection")
public class PhotoCorrection extends  Image{

   @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    @ManyToOne
    @JoinColumn(name="homework_id")
    private Homework homework;

    private String timestamp;

    Long idVersionHomework;
    String idProfessor;


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
