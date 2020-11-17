package it.polito.ai.virtualLabs.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
    private String password;
    @Column
    private String role;
    @Column
    private Boolean activate;

    @OneToOne
    @JoinColumn(name="student")
    Student student;

    @OneToOne
    @JoinColumn(name="professor")
    Professor professor;
}

