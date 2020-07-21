package it.polito.ai.virtualLabs.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "image_table")
public class Image {
    @Id
    private String id;
    private Timestamp timestamp;
    private String name;
    private String type;

    @OneToOne(mappedBy = "photoStudent")
    private Student student;
    @OneToOne(mappedBy = "photoProfessor")
    private  Professor professor;
    @OneToOne(mappedBy = "photoAssignment")
    private Assignment assignment;

    @OneToMany(mappedBy = "photoHomework")
    private List<Homework> homeworks = new ArrayList<>();

    @OneToOne(mappedBy = "screenshotModelVM")
    private ModelVM modelVM;



    @Column(name = "picByte", length = 1000)
    private byte[] picByte;

    public Image( String originalFilename, String contentType, byte[] compressZLib) {
        this.name = originalFilename;
        this.type = contentType;
        this.picByte = compressZLib;
    }
}
