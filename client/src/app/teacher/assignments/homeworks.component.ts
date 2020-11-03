import { CourseService } from './../../services/course.service';
import { TeacherService } from './../../services/teacher.service';
import { Component, OnInit, ViewChild, Input, Output, OnChanges, SimpleChanges, EventEmitter } from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Homework } from 'src/app/models/homework.model';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { Location } from '@angular/common';
import { Student } from 'src/app/models/student.model';
import { HomeworkStudent } from 'src/app/models/homework-student.model';

@Component({
  selector: 'app-homeworks',
  templateUrl: './homeworks.component.html',
  styleUrls: ['./homeworks.component.css']
})
export class HomeworksComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;

  private sort: MatSort;
  private paginator: MatPaginator;

  currentRoute: string;

  @Input() titolo:string;

  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSourceAttributes();
  }

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSourceAttributes();
  }

  displayedColumns: string[] = ['id', 'name', 'firstName', 'status', 'timestamp', 'grade', 'version'];
  dataSource = new MatTableDataSource<HomeworkStudent>();
  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  @Input() homeworkStudents: HomeworkStudent[];
  @Output('update') update = new EventEmitter<number>();

  homeworksToShow: any[] = [];
  versionsVisibility: boolean = false;
  tableVisibility: boolean = false;
  assId:string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private location: Location,
              private teacherService: TeacherService,
              private courseService: CourseService) {

    console.log(router.url);
    this.currentRoute = router.url;
    console.log(this.currentRoute);

    let paramRoute = this.currentRoute.split('/', 6);
    console.log(paramRoute);
    this.assId = paramRoute[5];
  }

  ngOnInit(): void {
    this.manageTableVisibility();
    this.manageTable();

    let course: string = this.courseService.currentCourse.getValue().name;


    this.teacherService.allAssignments(course).subscribe(
      (data) => {
        data.forEach(ass => {
          if(ass.id.toString() === this.assId){
            this.titolo = ass.assignmentName;
          }
        });
      },
      (error) => {
        window.alert(error.error.message);
      }
    );



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
    this.dataSource = new MatTableDataSource<HomeworkStudent>(this.homeworkStudents);
    this.setDataSourceAttributes();
    this.length = this.homeworkStudents.length;
  }

  setDataSourceAttributes() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  backAssignmetsTable() {
    this.location.back();
  }

  manageTableVisibility() {
    if (this.homeworkStudents.length > 0) {
      this.tableVisibility = true;
    }
    else {
      this.tableVisibility = false;
    }
  }

  onRouterOutletActivate(event: any) {
    this.versionsVisibility = true;
  }

  onRouterOutletDeactivate(event: any) {
    this.versionsVisibility = false;
    this.update.emit(this.route.snapshot.params.idA);
  }

  showHistory(hws: HomeworkStudent) {
    const homework = new Homework(hws.idHW, hws.status, hws.permanent, hws.grade, hws.timestamp);
    this.router.navigate([homework.id, 'versions'], { relativeTo: this.route, state: {homework: homework} });
  }

}
