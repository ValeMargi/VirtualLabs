export class HomeworkVersion {
    public id: number;
    public timestamp: string;
    public nameFile: string;

    constructor(id: number, timestamp: string, nameFile: string) {
        this.id = id;
        this.timestamp = timestamp;
        this.nameFile = nameFile;
    }
}