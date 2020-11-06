create table avatar_professor
(
    id        bigint       not null
        primary key,
    name_file varchar(255) null,
    pic_byte  mediumblob   null,
    type      varchar(255) null
);

create table avatar_student
(
    id        bigint       not null
        primary key,
    name_file varchar(255) null,
    pic_byte  mediumblob   null,
    type      varchar(255) null
);

create table hibernate_sequence
(
    next_val bigint null
);

create table photo_assignment
(
    id        bigint       not null
        primary key,
    name_file varchar(255) null,
    pic_byte  mediumblob   null,
    type      varchar(255) null,
    timestamp varchar(255) null
);

create table photo_modelvm
(
    id        bigint       not null
        primary key,
    name_file varchar(255) null,
    pic_byte  mediumblob   null,
    type      varchar(255) null
);

create table course
(
    name              varchar(255) not null
        primary key,
    acronym           varchar(255) null,
    disk_space        int          not null,
    enabled           bit          not null,
    max               int          not null,
    max_vcpu          int          not null,
    min               int          not null,
    ram               int          not null,
    running_instances int          not null,
    tot_instances     int          not null,
    image_id          bigint       null,
    constraint FKc1glqb5d9fem5g4pqlvvv40wn
        foreign key (image_id) references photo_modelvm (id)
);

create table assignment
(
    id              bigint       not null
        primary key,
    expiration      varchar(255) null,
    name_assignment varchar(255) null,
    release_date    varchar(255) null,
    course_id       varchar(255) null,
    image_id        bigint       null,
    constraint FKm2na9v5065hhk3sueg14nx6ja
        foreign key (image_id) references photo_assignment (id),
    constraint FKrop26uwnbkstbtfha3ormxp85
        foreign key (course_id) references course (name)
);

create table photovm
(
    id        bigint       not null
        primary key,
    name_file varchar(255) null,
    pic_byte  mediumblob   null,
    type      varchar(255) null
);

create table professor
(
    id         varchar(255) not null
        primary key,
    email      varchar(255) null,
    first_name varchar(255) null,
    name       varchar(255) null,
    image_id   bigint       null,
    constraint FK5at78bv2ixqgthg86d9ety805
        foreign key (image_id) references avatar_professor (id)
);

create table professor_course
(
    professor_id varchar(255) not null,
    course_name  varchar(255) not null,
    constraint FKngc752gy9764vvb40di6lause
        foreign key (professor_id) references professor (id),
    constraint FKojevjh95wgwd07u9bfii99un2
        foreign key (course_name) references course (name)
);

create table student
(
    id         varchar(255) not null
        primary key,
    email      varchar(255) null,
    first_name varchar(255) null,
    name       varchar(255) null,
    image_id   bigint       null,
    constraint FKdfbux8ji37ax79y0a90ntu4v5
        foreign key (image_id) references avatar_student (id)
);

create table homework
(
    id            bigint       not null
        primary key,
    grade         varchar(255) null,
    permanent     bit          null,
    status        varchar(255) null,
    assignment_id bigint       null,
    student_id    varchar(255) null,
    constraint FKckkx6ab94yfaxsjyo5iblj2xl
        foreign key (assignment_id) references assignment (id),
    constraint FKj2kmp9xbvs3l61bya88ljudfe
        foreign key (student_id) references student (id)
);

create table photo_correction
(
    id                  bigint       not null
        primary key,
    name_file           varchar(255) null,
    pic_byte            mediumblob   null,
    type                varchar(255) null,
    id_professor        varchar(255) null,
    id_version_homework bigint       null,
    timestamp           varchar(255) null,
    homework_id         bigint       null,
    constraint FK63f47inlg1205ccjme3m278l1
        foreign key (homework_id) references homework (id)
);

create table photo_version_homework
(
    id          bigint       not null
        primary key,
    name_file   varchar(255) null,
    pic_byte    mediumblob   null,
    type        varchar(255) null,
    timestamp   varchar(255) null,
    homework_id bigint       null,
    constraint FK56vo4k09uh3mnx941r6pxr26m
        foreign key (homework_id) references homework (id)
);

create table student_course
(
    student_id  varchar(255) not null,
    course_name varchar(255) not null,
    constraint FKfplmnau8umrux0cy6n01792qd
        foreign key (course_name) references course (name),
    constraint FKq7yw2wg9wlt2cnj480hcdn6dq
        foreign key (student_id) references student (id)
);

create table team
(
    id                bigint       not null
        primary key,
    creator_id        varchar(255) null,
    disk_space_left   int          not null,
    max_vcpu_left     int          not null,
    name              varchar(255) null,
    ram_left          int          not null,
    running_instances int          not null,
    status            int          not null,
    tot_instances     int          not null,
    course_id         varchar(255) null,
    constraint FKrdbahenwatuua698jkpnfufta
        foreign key (course_id) references course (name)
);

create table team_student
(
    team_id    bigint       not null,
    student_id varchar(255) not null,
    constraint FKcikvw8vwdt6jmeyksh25q60q
        foreign key (student_id) references student (id),
    constraint FKin4tsinuxmguuh6qvtue7oyti
        foreign key (team_id) references team (id)
);

create table token
(
    id          varchar(255) not null
        primary key,
    course_id   varchar(255) null,
    expiry_date datetime     null,
    status      bit          null,
    team_id     bigint       null,
    student_id  varchar(255) null,
    constraint FK1j9myk0fjdutkio0tf3mne3u9
        foreign key (student_id) references student (id)
);

create table token_registration
(
    id          varchar(255) not null
        primary key,
    expiry_date datetime     null,
    user_id     varchar(255) null
);

create table users
(
    id        varchar(255) not null
        primary key,
    activate  bit          null,
    password  varchar(255) null,
    role      varchar(255) null,
    professor varchar(255) null,
    student   varchar(255) null,
    constraint FK67djoa58von1506v76269jeey
        foreign key (professor) references professor (id),
    constraint FKpswj8g8g227rbwk4rqcqb3sxg
        foreign key (student) references student (id)
);

create table password_reset_token
(
    id          bigint       not null
        primary key,
    expiry_date datetime     null,
    token       varchar(255) null,
    user_id     varchar(255) not null,
    constraint FK83nsrttkwkb6ym0anu051mtxn
        foreign key (user_id) references users (id)
);

create table vm
(
    id         bigint       not null
        primary key,
    disk_space int          not null,
    namevm     varchar(255) null,
    num_vcpu   int          not null,
    ram        int          not null,
    status     varchar(255) null,
    timestamp  varchar(255) null,
    course_id  varchar(255) null,
    photo_id   bigint       null,
    team_id    bigint       null,
    constraint FK6kdulkg2bbmogmpjdoful5hwx
        foreign key (photo_id) references photovm (id),
    constraint FK9l5fpl8lpord3hrv5bu4cqb95
        foreign key (team_id) references team (id),
    constraint FKlat9rm2qdwpk641txn82ht1ub
        foreign key (course_id) references course (name)
);

create table vm_owners
(
    vm_id      bigint       not null,
    student_id varchar(255) not null,
    constraint FK90i2x78frnaff0xnqlmrq14p2
        foreign key (student_id) references student (id),
    constraint FKapy02mm97cdhuibybpk1x6not
        foreign key (vm_id) references vm (id)
);

create table vm_student
(
    vm_id      bigint       not null,
    student_id varchar(255) not null,
    constraint FKdkgtx3kok8lhm4wok7cvikfuf
        foreign key (vm_id) references vm (id),
    constraint FKsvoqe3i216j8wh47fhii986q8
        foreign key (student_id) references student (id)
);


