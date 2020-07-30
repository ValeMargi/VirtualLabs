export class Homework {
    public id: string;
    public status: string;
    public permanent: boolean;
    public grade: string;

    constructor(id: string, status: string, permanent: boolean, grade: string) {
        this.id = id; 
        this.status = status;
        this.permanent = permanent;
        this.grade = grade;
    }
}