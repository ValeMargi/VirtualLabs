export class Team {
    public id: number;
    public creatorId: string;
    public name: string;
    public status: number;
    public maxVcpuLeft: number;
    public diskSpaceLeft: number;
    public ramLeft: number;
    public runningInstances: number;
    public totInstances: number;

    constructor(id: number, creatorId: string, name: string, status: number, maxVcpuLeft: number, diskSpaceLeft: number, ramLeft: number, runningInstances: number, totInstances: number) {
        this.id = id;
        this.creatorId = creatorId;
        this.name = name;
        this.status = status; 
        this.maxVcpuLeft = maxVcpuLeft;
        this.diskSpaceLeft = diskSpaceLeft;
        this.ramLeft = ramLeft;
        this.runningInstances = runningInstances;
        this.totInstances = totInstances;
    }
}