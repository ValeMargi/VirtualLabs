import { Component, OnInit, ViewChild, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Homework } from 'src/app/models/homework.model';
import { ActivatedRoute, Router } from '@angular/router';
import { Student } from 'src/app/models/student.model';
import { HomeworkStudent } from 'src/app/models/homework-student.model';

@Component({
  selector: 'app-homeworks',
  templateUrl: './homeworks.component.html',
  styleUrls: ['./homeworks.component.css']
})
export class HomeworksComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['id', 'name', 'firstName', 'status', 'timestamp', 'grade'];
  dataSource = new MatTableDataSource<HomeworkStudent>();
  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  @Input() homeworkStudents: HomeworkStudent[];
  @Output() HOMEWORK: Homework;

  homeworksToShow: any[] = [];
  versionsVisibility: boolean = false;
  tableVisibility: boolean = false;

  constructor(private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.manageTableVisibility();
    this.manageTable();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.homeworkStudents != null) {
      this.homeworkStudents = changes.homeworkStudents.currentValue;
    }

    this.manageTableVisibility();
    this.manageTable();
  }

  getGrade(row: HomeworkStudent) {
    if (row.grade == null || row.grade == "-1") {
      return "Da valutare";
    }
    else {
      return row.grade;
    }
  }

  manageTable() {
    this.versionsVisibility = false;
    this.dataSource = new MatTableDataSource<HomeworkStudent>(this.homeworkStudents);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.length = this.homeworkStudents.length;
  }

  manageTableVisibility() {
    if (this.homeworkStudents.length > 0) {
      this.tableVisibility = true;
    }
    else {
      this.tableVisibility = false;
    }
  }

  showHistory(hws: HomeworkStudent) {
    this.versionsVisibility = true;
    let homework = new Homework(hws.idHW, hws.status, hws.permanent, hws.grade, hws.timestamp);
    this.HOMEWORK = homework;
    this.router.navigate([homework.id, 'versions'], { relativeTo: this.route });
  }

}
