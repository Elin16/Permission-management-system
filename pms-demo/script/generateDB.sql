/*CREATE DATABASE EntryPermissionDB;*/
CREATE TABLE campus
    (
        name varchar(30),
        primary key (name)
    );
CREATE TABLE department
    (
        ID numeric(11,0),
        name varchar(100),
        primary key(ID)
    );
CREATE TABLE class
    (
        ID numeric(11,0),
        name varchar(100) not null,
        dptID numeric(11,0) default null,
        primary key(ID),
        foreign key (dptID) references department(ID) on delete set null
    );
CREATE TABLE tutor
    (
        ID numeric(11,0),
        name varchar(100) not null,
        userpass varchar(30) not null,
        classID numeric(11,0) default null, /* reference key shuold be not null */
        primary key(ID),
        foreign key (classID) references class(ID) on delete set null
    );
CREATE TABLE admin
    (
        ID numeric(11,0),
        name varchar(100) not null,
        userpass varchar(30) not null,
        dptID numeric(11,0),
        primary key(ID),
        foreign key (dptID) references department(ID) on delete set null
    );
CREATE TABLE superAdmin
(
    ID numeric(11,0),
    name varchar(100) not null,
    userpass varchar(30) not null,
    primary key(ID)
);
CREATE TABLE student
    (
        ID numeric(11,0) check (ID > 0),
        name varchar(30) not null,
        userpass varchar(30) not null,
        entryPerm boolean not null default 0,
        inSchool boolean not null default 0,

        classID numeric(11,0) default null,
        email varchar(30),
        domitory varchar(30),

        telephoneNum varchar(30),
        homeAddress varchar(100),
        indentityType varchar(30) check (indentityType in ('Passport','SAR','Citizen ID Card')),
        indentityNum numeric(30,0) not null,
        liveIn varchar(30) default null,
        primary key (ID),
        foreign key (liveIn) references campus(name) on delete set null,
        foreign key (classID) references class(ID) on delete set null
    );
CREATE TABLE healthLog
    (
        studentID  numeric(11,0),
        reportDate timestamp,
        bodyTemperature numeric(2,1) not null, 
        onLocation varchar(200) not null,
        comments varchar(500),
        primary key(studentID,reportDate),
        foreign key(studentID) references student(ID) on delete cascade
    );

CREATE TABLE leaveApplication
    (
        ID numeric(30,0),
        studentID  numeric(11,0),
        exceptLeaveTime timestamp not null,
        exceptReturnTime timestamp not null,
        reason varchar(500) not null,
        destination varchar(200) not null,
        progress varchar(30) check (progress in ('submitted','tutor approved','fail','success')) default 'submitted',
        refuseReson varchar(500),
        primary key(ID),
        foreign key(studentID) references student(ID) on delete cascade
    );

CREATE TABLE entryApplication
    (
        ID numeric(30,0),
        studentID  numeric(11,0) default null,
        travelHistoryList varchar(500) not null, 
        exceptEntryTime timestamp not null,
        reason varchar(500) not null,
        progress varchar(30) check (progress in ('submitted','tutor approved','fail','success')),
        refuseReson varchar(500),
        primary key(ID),
        foreign key(studentID) references student(ID) on delete cascade
    );
CREATE TABLE IOLog
    (
        studentID numeric(11,0), 
        IOTime timestamp,
        IOType varchar(3) check (IOType in ('in','out')) default 'out',
        campusName varchar(30),
        primary key(studentID,IOTime),
        foreign key(studentID) references student(ID) on delete cascade,
        foreign key(campusName) references campus(name) on delete cascade
    );
