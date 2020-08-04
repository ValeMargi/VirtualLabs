package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Team {
    //@Id @GeneratedValue(generator="optimized-sequence")
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private  String name;
    private int status, maxVpcuLeft, diskSpaceLeft, ramLeft, runningInstances, totInstances;


    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "team_student",
            joinColumns = @JoinColumn(name="team_id"),
            inverseJoinColumns = @JoinColumn(name="student_id"))
    List<Student> members = new ArrayList<>();

    @ManyToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="course_id")
    Course course;

    @OneToMany(mappedBy = "team")
    private List<VM> vms = new ArrayList<>();

    public void addStudentIntoTeam(Student s) {
        if (!members.contains(s)) {
            members.add(s);
            s.setTeamForStudent(this);
        }

    }
    public void removeStudentFromTeam(Student s){
        if( members.contains(s)){
            members.remove(s);
            s.removeTeamForStudent(this);
        }
    }

    public void removeVM(VM vm){
        if(vm!=null && vms.contains(vm)){
            vms.remove(vm);
        }
    }

}
