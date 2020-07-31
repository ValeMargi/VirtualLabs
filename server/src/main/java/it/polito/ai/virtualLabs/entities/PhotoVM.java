package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity(name = "PhotoVM")
public class PhotoVM extends Image{

    @OneToOne(mappedBy = "photoVM")
    private VM vm;

    private Timestamp timestamp;

    public  void setVM(VM v){
        if(v!=null && vm!=v){
            vm=v;
            v.photoVM(this);
        }
    }
    public  PhotoVM( Image image){
        super(image);
    }
}
