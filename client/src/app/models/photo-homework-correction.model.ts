export class PhotoHomeworkCorrection {
    public id: number;
    public idVersionHomework: number;
    public idProfessor: string;
    public nameFile: string;
    public type: string;
    public picByte: Uint8Array;
    public timestamp: string;

    constructor(id: number, idVersionHomework: number, idProfessor: string, nameFile: string, type: string, picByte: Uint8Array, timestamp: string) {
        this.id = id;
        this.idVersionHomework = idVersionHomework;
        this.idProfessor = idProfessor;
        this.nameFile = nameFile;
        this.type = type;
        this.picByte = picByte;
        this.timestamp = timestamp;
    }
}