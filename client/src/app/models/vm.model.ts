export class VM {
    public id: number;
    public numVcpu: number;
    public diskSpace: number;
    public ram: number;
    public status: string;
    public nameVM: string;
    public timestamp: string;

    constructor (id:number, numVcpu: number, diskSpace: number, ram: number, status: string, nameVM: string, timestamp: string) {
        this.id = id;
        this.numVcpu = numVcpu;
        this.diskSpace = diskSpace;
        this.ram = ram;
        this.status = status;
        this.nameVM = nameVM;
        this.timestamp = timestamp;
    }
}