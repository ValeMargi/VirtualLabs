import { Student } from './student.model';
import { Team } from './team.model';
import { Teacher } from './teacher.model';

export class Course {
    public id: string;
    public name: string;
    public min: number;
    public max: number;
    public enabled: boolean;
    public teacher: Teacher;
    public students: Student[];
    public teams: Team[];

    constructor(id: string, name: string, min: number, max: number, enabled: boolean, teacher: Teacher, students: Student[], teams: Team[]) {
        this.id = id;
        this.name = name;
        this.min = min;
        this.max = max;
        this.enabled = enabled;
        this.teacher = teacher;
        this.students = students;
        this.teams = teams;
    }
    
}