export class Course {
    public name: string;
    public acronym: string;
    public min: number;
    public max: number;
    public enabled: number;
    public maxVcpu: number;
    public diskSpace: number;
    public ram: number;
    public runningInstances: number;
    public totInstances: number;

    constructor(name: string, acronym: string, min: number, max: number, enabled: number, maxVcpu: number, diskSpace: number, ram: number, runningInstances: number, totInstances: number) {
        this.name = name;
        this.acronym = acronym;
        this.min = min;
        this.max = max;
        this.enabled = enabled;
        this.maxVcpu = maxVcpu;
        this.diskSpace = diskSpace;
        this.ram = ram;
        this.runningInstances = runningInstances;
        this.totInstances = totInstances;
    }
}