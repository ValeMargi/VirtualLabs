export class Assignment {
    public id: string;
    public name: string;
    public teacherId: string;
    public courseId: string;
    public release: string;
    public expiration: string;

    constructor(id: string, name: string, teacherId: string, courseId: string, release: string, expiration: string) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
        this.courseId = courseId;
        this.release = release;
        this.expiration = expiration;
    }
}