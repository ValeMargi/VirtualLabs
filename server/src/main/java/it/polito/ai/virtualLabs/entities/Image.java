package it.polito.ai.virtualLabs.entities;

import lombok.Data;
import javax.persistence.*;

@Data
@MappedSuperclass
public  class Image {

    private String nameFile;
    private String type;
    @Column(name = "picByte", length = 16777215)
    private byte[] picByte;

    public Image( String originalFilename, String contentType, byte[] compressZLib) {
        this.nameFile = originalFilename;
        this.type = contentType;
        this.picByte = compressZLib;
    }

    public Image(Image image) {
        this.nameFile = image.nameFile;
        this.type = image.type;
        this.picByte = image.picByte;

    }

    public Image(){}
}
