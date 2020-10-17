package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity(name = "PhotoVM")
public class PhotoVM extends Image{

   @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    @OneToOne(mappedBy = "photoVM")
    private VM vm;


    void setVM(VM v){
        if(v!=null && vm!=v){
            vm=v;
            v.setPhotoVM(this);
        }
    }
    public  PhotoVM( Image image){
        super(image);
    }
}
