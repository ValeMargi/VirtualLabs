export class AssignmentGrade {
    public id: number;
    public assignmentName: string;
    public releaseDate: string;
    public expiration: string;
    public grade: string;
    public status: string;

    constructor(id: number, assignmentName: string, releaseDate: string, expiration: string, grade: string, status: string) {
        this.id = id;
        this.assignmentName = assignmentName;
        this.releaseDate = releaseDate;
        this.expiration = expiration;
        this.grade = grade;
        this.status = status;
    }
}