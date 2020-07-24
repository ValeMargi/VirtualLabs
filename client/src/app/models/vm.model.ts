export class VM {
    public id: string;
    public numVcpu: number;
    public diskSpace: number;
    public ram: number;
    private status: string;

    constructor (id:string, numVcpu: number, diskSpace: number, ram: number, status: string) {
        this.id = id;
        this.numVcpu = numVcpu;
        this.diskSpace = diskSpace;
        this.ram = ram;
        this.status = status;
    }
}