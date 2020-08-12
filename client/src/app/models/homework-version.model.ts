export class HomeworkVersion {
    public id: number;
    public timestamp: string;
    public nameFile: string;
    public type: string;
    public picByte: Uint8Array;

    constructor(id: number, timestamp: string, nameFile: string, type: string, picByte: Uint8Array) {
        this.id = id;
        this.timestamp = timestamp;
        this.nameFile = nameFile;
        this.type = type;
        this.picByte = picByte;
    }
}