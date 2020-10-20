export class Homework {
    public id: number;
    public status: string;
    public permanent: boolean;
    public grade: string;
    public timestamp: string;

    constructor(id: number, status: string, permanent: boolean, grade: string, timestamp: string) {
        this.id = id; 
        this.status = status;
        this.permanent = permanent;
        this.grade = grade;
        this.timestamp = timestamp;
    }
}