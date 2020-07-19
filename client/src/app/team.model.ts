import { Course } from './course.model';
import { Student } from './student.model';

export class Team {
    public id: string;
    public name: string;
    public status: number;
    public courseId: number;
    public members: Student[];

    constructor(id: string, name: string, status: number, courseId: number, members: Student[]) {
        this.id = id;
        this.name = name;
        this.status = status; 
        this.courseId = courseId;
        this.members = members;
    }
}