import { Component, OnInit, ViewChild, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Homework } from 'src/app/models/homework.model';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-homeworks',
  templateUrl: './homeworks.component.html',
  styleUrls: ['./homeworks.component.css']
})
export class HomeworksComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['id', 'name', 'firstName', 'status', 'timestamp'];
  dataSource = new MatTableDataSource<Homework>();
  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  @Input() public homeworks: Homework[] = [];
  @Output() public HOMEWORK: Homework;

  versionsVisibility: boolean = false;
  tableVisibility: boolean = false;

  constructor(private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.manageTableVisibility();
    this.manageTable();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.homeworks = changes.homeworks.currentValue;
    this.manageTableVisibility();
    this.manageTable();
  }

  manageTable() {
    this.versionsVisibility = false;
    this.dataSource = new MatTableDataSource<Homework>(this.homeworks);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.length = this.homeworks.length;
  }

  manageTableVisibility() {
    if (this.homeworks.length > 0) {
      this.tableVisibility = true;
    }
    else {
      this.tableVisibility = false;
    }
  }

  showHistory(homework: Homework) {
    this.versionsVisibility = true;
    this.HOMEWORK = homework;
    this.router.navigate([homework.id, 'versions'], { relativeTo: this.route });
  }

}
