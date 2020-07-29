package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ModelVM {
    /*
    @Id
    private String id;

    private int maxVcpu, diskSpace, ram, runningInstances, totInstances;

    @OneToOne(mappedBy = "modelVM")
    private Course course;

    @OneToMany(mappedBy = "vm")
    private List<VM> vms = new ArrayList<>();

    @OneToOne
    @JoinColumn(name="image_id")
    Image screenshot;

    public void setCourse(Course c){
       this.course =c;
       c.modelVM=this;
    }

    public  void removeVM(VM vm){
        if(vm!=null && vms.contains(vm)){
            vms.remove(vm);
        }
    }

    public void setScreenshot(Image i){
        if(i!=null && screenshot!=i){
            screenshot=i;
            i.setModelVM(this);
        }
    }
*/
}

