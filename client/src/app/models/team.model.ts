import { Course } from './course.model';
import { Student } from './student.model';

export class Team {
    public id: string;
    public name: string;
    public status: number;

    constructor(id: string, name: string, status: number) {
        this.id = id;
        this.name = name;
        this.status = status; 
    }
}