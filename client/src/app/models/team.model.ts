export class Team {
    public id: number;
    public creatorId: string;
    public name: string;
    public status: number;
    public maxVcpuLeft: number;
    public diskSpaceLeft: number;
    public ramLeft: number;
    public runningInstancesLeft: number;
    public totInstancesLeft: number;

    constructor(id: number, creatorId: string, name: string, status: number, maxVcpuLeft: number, diskSpaceLeft: number, ramLeft: number, runningInstancesLeft: number, totInstancesLeft: number) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.status = status; 
        this.maxVcpuLeft = maxVcpuLeft;
        this.diskSpaceLeft = diskSpaceLeft;
        this.ramLeft = ramLeft;
        this.runningInstancesLeft = runningInstancesLeft;
        this.totInstancesLeft = totInstancesLeft;
    }
}