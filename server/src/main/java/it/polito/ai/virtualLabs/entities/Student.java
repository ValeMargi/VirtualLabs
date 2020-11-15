package it.polito.ai.virtualLabs.entities;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"photoStudent", "courses"})
public class Student{
    @Id
    @CsvBindByName
    private String id;
    @CsvBindByName
    private String name, firstName;
    @Email
    @CsvBindByName
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

    @OneToOne(mappedBy = "student")
    private UserDAO userDAO;

    @OneToMany(mappedBy="student")
    private List<Token> tokens =new ArrayList<>();


    public  void setCourses(Course c){
        if(c!=null && !courses.contains(c)){
            courses.add(c);
            c.addStudent(this);
        }
    }

    public  void removeCourses(Course c){
        if(c!=null && courses.contains(c)){
            courses.remove(c);
            c.removeStudent(this);
        }
    }

    public void setPhotoStudent(AvatarStudent avatarStudent){
        if(avatarStudent!=null && photoStudent!=avatarStudent){
            photoStudent = avatarStudent;
            avatarStudent.setStudent(this);
        }
    }


    public  void setTeamForStudent(Team t){
        if(t!=null && !teams.contains(t)){
            teams.add(t);
            t.addStudentIntoTeam(this);
        }
    }

    public  void removeTeamForStudent(Team t){
        if(t!=null && teams.contains(t)){
            teams.remove(t);
            t.removeStudentFromTeam(this);
        }
    }

    public void setHomeworkForStudent(Homework h){
        if(h!=null && !homeworks.contains(h))
        {
            homeworks.add(h);
            h.setStudent(this);
        }
    }

    public void removeHomeworkStudent(Homework h){
        if(h!=null && homeworks.contains(h)){
            homeworks.remove(h);
            h.removeStudentFromHomework(this);
        }
    }

    public void addOwnerToVM(VM vm){
        if(vm!=null && !ownersVM.contains(vm)){
            ownersVM.add(vm);
            vm.addStudentToOwnerList(this);
        }
    }

    public void addMemberToVM(VM vm){
        if(vm!=null && !studentsVM.contains(vm)){
            studentsVM.add(vm);
            vm.addStudentToMemberList(this);
        }
    }

    public void removeOwnerToVM(VM vm){
        if(vm!=null && ownersVM.contains(vm)){
            ownersVM.remove(vm);
            vm.removeStudentToOwnerList(this);
        }
    }

    public void removeMemberToVM(VM vm) {
        if (vm != null && studentsVM.contains(vm)) {
            studentsVM.remove(vm);
            vm.removeStudentToMemberList(this);
        }
    }

    public void setTokenForStudent(Token t){
         if(t!=null && !tokens.contains(t))
         {
              tokens.add(t);
              t.setStudentToken(this);
         }
    }

    public void removeToken(Token t){
        if(t!=null && tokens.contains(t))
        {
            tokens.remove(t);
        }
    }

}
