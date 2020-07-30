package it.polito.ai.virtualLabs.entities;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import org.apache.commons.beanutils.PropertyUtilsBean;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Student {
    @Id
    @CsvBindByName
    private String id;
    @CsvBindByName
    private String name, firstName;
    @Email
    private String email;

    @ManyToMany(mappedBy ="members")
    private List<Team> teams = new ArrayList<>();

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="student_course",
            joinColumns = @JoinColumn(name="student_id"),
            inverseJoinColumns = @JoinColumn(name="course_name"))
    private List<Course> courses = new ArrayList<>();

    @ManyToMany(mappedBy ="membersVM")
    private List<VM> studentsVM = new ArrayList<>();
    @ManyToMany(mappedBy ="ownersVM")
    private List<VM> ownersVM = new ArrayList<>();

    @OneToOne //(fetch = FetchType.EAGER) default
    @JoinColumn(name="image_id")
    AvatarStudent photoStudent;

    @OneToMany(mappedBy = "student")
    private List<Homework> homeworks = new ArrayList<>();

    @OneToOne(mappedBy = "studentEmail")
    private UserDAO userDAO;


    public void setPhotoStudent(AvatarStudent avatarStudent){
        if(avatarStudent!=null){
            photoStudent = avatarStudent;
            avatarStudent.setStudent(this);
        }
    }

    public void setHomeworkForStudent(Homework h){
        if(h!=null && !homeworks.contains(h))
        {
            homeworks.add(h);
            h.setStudent(this);
        }
    }

    public void addOwnerToVM(VM vm){
        if(vm!=null && !ownersVM.contains(vm)){
            ownersVM.add(vm);
            vm.getOwnersVM().add(this);
        }
    }

    public void addMemberToVM(VM vm){
        if(vm!=null && !studentsVM.contains(vm)){
            studentsVM.add(vm);
            vm.getMembersVM().add(this);
        }
    }

    public void removeOwnerToVM(VM vm){
        if(vm!=null && ownersVM.contains(vm)){
            ownersVM.remove(vm);
            vm.getOwnersVM().remove(this);
        }
    }

    public void removeMemberToVM(VM vm){
        if(vm!=null && studentsVM.contains(vm)){
            studentsVM.remove(vm);
            vm.getMembersVM().remove(this);
        }
    }
}
