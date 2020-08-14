export class Team {
    public id: number;
    public name: string;
    public status: number;
    public maxVpcuLeft: number;
    public diskSpaceLeft: number;
    public ramLeft: number;
    public runningInstances: number;
    public totInstances: number;

    constructor(id: number, name: string, status: number, maxVpcuLeft: number, diskSpaceLeft: number, ramLeft: number, runningInstances: number, totInstances: number) {
        this.id = id;
        this.name = name;
        this.status = status; 
        this.maxVpcuLeft = maxVpcuLeft;
        this.diskSpaceLeft = diskSpaceLeft;
        this.ramLeft = ramLeft;
        this.runningInstances = runningInstances;
        this.totInstances = totInstances;
    }
}