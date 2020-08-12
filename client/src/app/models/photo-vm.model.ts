export class PhotoVM {
    public id: number;
    public nameFile: string;
    public type: string;
    public picByte: Uint8Array;

    constructor(id: number, nameFile: string, type: string, picByte: Uint8Array) {
        this.id = id;
        this.nameFile = nameFile;
        this.type = type;
        this.picByte = picByte;
    }
}