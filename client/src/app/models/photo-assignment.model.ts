export class PhotoAssignment {
    public id: number;
    public nameFile: string;
    public type: string;
    public picByte: Uint8Array;
    public timestamp: string;

    constructor(id: number, nameFile: string, type: string, picByte: Uint8Array, timestamp: string) {
        this.id = id;
        this.nameFile = nameFile;
        this.type = type;
        this.picByte = picByte;
        this.timestamp = timestamp;
    }
}