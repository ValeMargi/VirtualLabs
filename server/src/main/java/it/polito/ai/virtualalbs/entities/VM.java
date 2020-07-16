package it.polito.ai.virtualalbs.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class VM {
    @Id
    private String id;
    private int  maxVpcu, diskSpace, ram;
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

    @OneToOne(mappedBy = "vm")
    private Team team;
}
