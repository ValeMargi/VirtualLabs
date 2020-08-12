export class Homework {
    public id: number;
    public status: string;
    public permanent: boolean;
    public grade: string;

    constructor(id: number, status: string, permanent: boolean, grade: string) {
        this.id = id; 
        this.status = status;
        this.permanent = permanent;
        this.grade = grade;
    }
}