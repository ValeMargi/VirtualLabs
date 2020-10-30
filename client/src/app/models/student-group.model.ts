export class StudentGroup {
	public id: string;
	public firstName: string;
	public name: string;
    public email: string;
    public teamName: string;

	constructor(id: string, firstName: string, name: string, email: string, teamName: string) {
		this.id = id;
		this.firstName = firstName;
		this.name = name;
        this.email = email;
        this.teamName = teamName;
	}
}