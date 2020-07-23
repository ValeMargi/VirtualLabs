package it.polito.ai.virtualLabs.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserDAO {
    @Id
    @Column
    @Email
    private String email;
    @Column
    @JsonIgnore
    private String password;
    @Column
    private  String role;

    @OneToOne
    @JoinColumn(name="student_email")
    Student studentEmail;

    @OneToOne
    @JoinColumn(name="professor_email")
    Professor professorEmail;






}

