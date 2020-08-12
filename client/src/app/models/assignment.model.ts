export class Assignment {
    public id: string;
    public assignmentName: string;
    public teacherId: string;
    public courseId: string;
    public releaseDate: string;
    public expiration: string;

    constructor(id: string, assignmentName: string, teacherId: string, courseId: string, releaseDate: string, expiration: string) {
        this.id = id;
        this.assignmentName = assignmentName;
        this.teacherId = teacherId;
        this.courseId = courseId;
        this.releaseDate = releaseDate;
        this.expiration = expiration;
    }
}