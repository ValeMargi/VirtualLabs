import { Student } from './student.model';

export class HomeworkVersion {
    public student: Student;
    public status: number;
    public timestamp: string;

    constructor(student: Student, status: number, timestamp: string) {
        this.student = student;
        this.status = status;
        this.timestamp = timestamp;
    }
}