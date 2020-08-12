export class HomeworkCorrection {
    public id: number;
    public idVersionHomework: number;
    public idProfessor: string;
    public timestamp: string;
    public nameFile: string;
    public type: string;
    public picByte: Uint8Array;

    constructor(id: number, idVersionHomework: number, idProfessor: string, timestamp: string, nameFile: string, type: string, picByte: Uint8Array) {
        this.id = id;
        this.idVersionHomework = idVersionHomework;
        this.idProfessor = idProfessor;
        this.timestamp = timestamp;
        this.nameFile = nameFile;
        this.type = type;
        this.picByte = picByte;
    }
}