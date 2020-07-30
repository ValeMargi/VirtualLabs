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
//@Entity
//@Table(name = "image_table")
@MappedSuperclass
public  class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
   // @Column(name = “id”, updatable = false, nullable = false)
    private Long id;
    private String name;
    private String type;
    @Column(name = "picByte", length = 1000)
    private byte[] picByte;


    public Image( String originalFilename, String contentType, byte[] compressZLib) {
        this.name = originalFilename;
        this.type = contentType;
        this.picByte = compressZLib;
    }

    public Image(Image image) {
        this.name = image.name;
        this.type = image.type;
        this.picByte = image.picByte;

    }





}
