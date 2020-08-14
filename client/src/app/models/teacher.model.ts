export class Teacher {
    public id: string;
    public fistName: string;
    public name: string;
    public email: string;

    constructor(id: string, firstName: string, name: string, email: string) {
        this.id = id;
        this.fistName = firstName;
        this.name = name;
        this.email = email;
    }
}