export class HomeworkCorrection {
    public id: number;
    public timestamp: string;
    public nameFile: string;
    public versionId: number;

    constructor(id: number, timestamp: string, nameFile: string, versionId: number) {
        this.id = id;
        this.timestamp = timestamp;
        this.nameFile = nameFile;
        this.versionId = versionId;
    }
}