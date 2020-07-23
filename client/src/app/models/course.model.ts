import { Student } from './student.model';
import { Team } from './team.model';
import { Teacher } from './teacher.model';

export class Course {
    public id: string;
    public name: string;
    public min: number;
    public max: number;
    public enabled: boolean;

    constructor(id: string, name: string, min: number, max: number, enabled: boolean) {
        this.id = id;
        this.name = name;
        this.min = min;
        this.max = max;
        this.enabled = enabled;
    }
    
}