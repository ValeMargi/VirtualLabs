export class Proposal {
    public creator: string;
    public teamStatus: string;
    public status: string;
    public students: any[];
    public teamName: string;
    public tokenId: string;

    constructor(creator: string, teamStatus:string,  status: string, students: any[], teamName: string, tokenId: string) {
        this.creator = creator;
        this.teamStatus = teamStatus;
        this.status = status;
        this.students = students;
        this.teamName = teamName;
        this.tokenId = tokenId;
    }
}
