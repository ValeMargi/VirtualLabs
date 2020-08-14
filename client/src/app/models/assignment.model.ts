export class Assignment {
    public id: number;
    public assignmentName: string;
    public releaseDate: string;
    public expiration: string;

    constructor(id: number, assignmentName: string, releaseDate: string, expiration: string) {
        this.id = id;
        this.assignmentName = assignmentName;
        this.releaseDate = releaseDate;
        this.expiration = expiration;
    }
}