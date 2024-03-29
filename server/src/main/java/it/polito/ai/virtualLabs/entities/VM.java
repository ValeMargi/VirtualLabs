package it.polito.ai.virtualLabs.entities;

import lombok.Data;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class VM{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    private String nameVM;
    private int  numVcpu, diskSpace, ram;
    private String status;


    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "vm_student",
            joinColumns = @JoinColumn(name="vm_id"),
            inverseJoinColumns = @JoinColumn(name="student_id"))
    List<Student> membersVM = new ArrayList<>();

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "vm_owners",
            joinColumns = @JoinColumn(name="vm_id"),
            inverseJoinColumns = @JoinColumn(name="student_id"))
    List<Student> ownersVM = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="course_id")
    Course course;

    @ManyToOne
    @JoinColumn(name="team_id")
    Team team;

    @OneToOne
    @JoinColumn(name="photo_id")
    PhotoVM photoVM;

    public boolean addStudentToOwnerList(Student s){
        if(s!=null && !ownersVM.contains(s)){
            ownersVM.add(s);
            s.addOwnerToVM(this);
            return true;
        }
        return false;
    }

    public boolean addStudentToMemberList(Student s){
        if(s!=null && !membersVM.contains(s)){
            membersVM.add(s);
            s.addMemberToVM(this);
            return true;
        }
        return false;
    }

    public boolean removeStudentToOwnerList(Student s){
        if(s!=null && ownersVM.contains(s)){
            ownersVM.remove(s);
            s.removeOwnerToVM(this);
            return true;
        }
        return false;
    }

    public boolean removeStudentToMemberList(Student s){
        if(s!=null && membersVM.contains(s)){
            membersVM.remove(s);
            s.removeMemberToVM(this);
            return true;
        }
        return false;
    }

    public void setPhotoVM(PhotoVM p){
        if(p!=null && photoVM!=p){
            photoVM=p;
            p.setVM(this);
        }
    }

    public void setCourse(Course c){
        if(c!=null && c!=course){
            course = c;
            c.addVM(this);
        }
    }

    public  void setTeam(Team t){
        if(t!=null && team!=t){
            team=t;
            t.addVM(this);
        }
    }
}
