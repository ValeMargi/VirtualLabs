export class User {
    public id: number;
    public password: string;
    public role: string;
    public activate: boolean;

    constructor(id: number, password: string, role: string, activate: boolean) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.activate = activate;
    }
    
}