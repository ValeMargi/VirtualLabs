export class Proposal {
    public creator: string;
    public status: boolean;
    public students: any[];
    public teamName: string;
    public tokenId: string;

    constructor(creator: string, status: boolean, students: any[], teamName: string, tokenId: string) {
        this.creator = creator;
        this.status = status;
        this.students = students;
        this.teamName = teamName;
        this.tokenId = tokenId;
    }
}
