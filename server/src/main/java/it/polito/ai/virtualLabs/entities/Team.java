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
    /**
     * la stringa status può avere 3 valori:
     *  - "active": se tutti gli studenti hanno accettato l'invito e il team è stato attivato
     *  - "pending": se uno o più studenti non hanno ancora rispto all'invito di partecipazione al team
     *  - "disabled": se uno studente ha rifiutato l'invito al team
     */
    private String status;
    private int maxVcpuLeft, diskSpaceLeft, ramLeft, runningInstancesLeft, totInstancesLeft;
    /**
     * timestamp che indica la data in cui il team è passato allo stato "disabled" (perché uno studente ha rifiutato o
     * perché la data di scadenza della proposta è stata superata). Viene utilizzato per eliminare la proposta dopo un intervallo
     * di tempo.
     */
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
