import { Student } from './student.model';

export class VMOwners {
    public id: number;
    public numVcpu: number;
    public diskSpace: number;
    public ram: number;
    public status: string;
    public nameVM: string;
    public timestamp: string;
    public owners: Student[];

    constructor (id:number, numVcpu: number, diskSpace: number, ram: number, status: string, nameVM: string, timestamp: string, owners: Student[]) {
        this.id = id;
        this.numVcpu = numVcpu;
        this.diskSpace = diskSpace;
        this.ram = ram;
        this.status = status;
        this.nameVM = nameVM;
        this.timestamp = timestamp;
        this.owners = owners;
    }
}