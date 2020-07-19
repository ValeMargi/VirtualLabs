import { Student } from './student.model';
import { Team } from './team.model';

export class VM {
    public id: string;
    public name: string;
    public teamId: string;
    public on: boolean;
    public ownerId: string;
    public studentIds: string[];

    constructor (id:string, name:string, teamId: string, on:boolean, ownerId: string, studentIds: string[]) {
        this.id = id;
        this.name = name;
        this.teamId = teamId;
        this.on = on;
        this.ownerId = ownerId;
        this.studentIds = studentIds;
    }
}