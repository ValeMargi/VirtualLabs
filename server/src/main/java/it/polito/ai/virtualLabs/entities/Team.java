package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Team {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private  String name, creatorId;
    private String status; /*active, pending, disabled*/
    private int maxVcpuLeft, diskSpaceLeft, ramLeft, runningInstancesLeft, totInstancesLeft;
    private String disabledTimestamp;

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "team_student",
            joinColumns = @JoinColumn(name="team_id"),
            inverseJoinColumns = @JoinColumn(name="student_id"))
    List<Student> members = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="course_id")
    Course course;

    @OneToMany(mappedBy = "team")
    private List<VM> vms = new ArrayList<>();

    public void removeCourse(Course c){
        if( course == c){
            course=null;
            c.removeTeam(this);
        }
    }


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

    public void addVM(VM vm) {
        if(vm!=null && !vms.contains(vm)){
            vms.add(vm);
            vm.setTeam(this);
        }
    }
}
