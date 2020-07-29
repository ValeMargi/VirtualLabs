package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import org.springframework.ui.Model;

import javax.persistence.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Data
@Entity
@Table(name = "image_table")
public class Image {
    @Id
    @GeneratedValue
    private Long id;
    private Timestamp timestamp;
    private String name;
    private String type;

    @OneToOne(mappedBy = "photoStudent")
    private Student student;
    @OneToOne(mappedBy = "photoProfessor")
    private  Professor professor;
    @OneToOne(mappedBy = "photoAssignment")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name="homework_id")
    private Homework homework;

    @OneToOne(mappedBy = "screenshotModelVM")
    private Course course;

    @OneToOne(mappedBy = "screenshotVM")
    private VM vm;



    @Column(name = "picByte", length = 1000)
    private byte[] picByte;

    public Image( String originalFilename, String contentType, byte[] compressZLib) {
        this.name = originalFilename;
        this.type = contentType;
        this.picByte = compressZLib;
    }

    public void setAssignment(Assignment a){
        if( a!=null && assignment != a){
            assignment =a;
            a.setImageAssignment(this);
        }
    }

    public void setHomework(Homework h){
        if( h!=null && homework!=h){
            homework=h;
            h.getImages().add(this);
        }
    }

    public  void setStudent(Student s){
        if(s!=null){
            student = s;
            s.setPhotoStudent(this);
        }
    }

    public  void setProfessor(Professor p){
        if(p!=null){
            professor = p;
            p.setPhotoProfessor(this);
        }
    }

    public  void setScreenshotModelVM(Course c){
        if(c!=null && course!=c){
            course=c;
            c.setScreenshotModelVM(this);
        }
    }

    public  void setVM(VM v){
        if(v!=null && vm!=v){
            vm=v;
            v.setScreenshot(this);
        }
    }



}
