export class HomeworkStudent {
    public idS: string;
	public firstName: string;
	public name: string;
	public email: string;
    public idHW: number;
    public status: string;
    public permanent: boolean;
    public grade: string;
    public timestamp: string;

    constructor(idS: string, firstName: string, name: string, email: string, idHW: number, status: string, permanent: boolean, grade: string, timestamp: string) {
        this.idS = idS;
        this.firstName = firstName;
        this.name = name;
        this.email = email;
        this.idHW = idHW;
        this.status = status;
        this.permanent = permanent;
        this.grade = grade;
        this.timestamp = timestamp;
    }
}