export class Modelvm {
    public id: string;
    public maxVcpu: number;
    public diskSpace: number;
    public ram: number;
    public runningInstances: number;
    public totInstances: number;

    constructor(id: string, maxVcpu: number, diskSpace: number, ram: number, runningInstances: number, totInstances: number) {
        this.id = id;
        this.maxVcpu = maxVcpu;
        this.diskSpace = diskSpace;
        this.ram = ram;
        this.runningInstances = runningInstances;
        this.totInstances = totInstances;
    }
}
