import { Course } from './course.model';

export class Teacher {
    public id: string;
    public name: string;
    public fistName: string;
    public courses: Course[];

    constructor(id: string, name: string, firstName: string, courses: Course[]) {
        this.id = id;
        this.name = name;
        this.fistName = firstName;
        this.courses = courses;
    }
}