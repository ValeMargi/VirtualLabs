package it.polito.ai.virtualLabs.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserDAO {
    @Id
    @Column
    private String id;
    @Column
    @JsonIgnore
    @Min(value = 8)
    @Max(value=20)
    private String password;
    @Column
    private  String role;
    @Column
    private Boolean activate;

    @OneToOne
    @JoinColumn(name="student")
    Student student;

    @OneToOne
    @JoinColumn(name="professor")
    Professor professor;







}

