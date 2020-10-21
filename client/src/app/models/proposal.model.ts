export class Proposal {
    public creator: string;
    public students: any[];
    public teamName: string;
    public tokenId: string;

    constructor(creator: string, students: any[], teamName: string, tokenId: string) {
        this.creator = creator;
        this.students = students;
        this.teamName = teamName;
        this.tokenId = tokenId;
    }
}