# The following sql sentences have been checked in phpmyadmin.
# 1.1 student
select *
from healthLog
where (studentID='19307090001' and datediff(curdate(), reportDate) < 5);

# 1.1 tutor
select *
from healthLog
where (studentID in(
    select studentID
    from student
    where classID in (
        select classID
        from tutor
        where id='1'
        )
    ) and datediff(curdate(), reportDate) < 5);

# 1.2 student
select entryPerm
from student
where ID='19307090001';

# 1.3 student
select *
from entryApplication
where studentID='19307090001' and progress='submitted';

select *
from leaveApplication
where studentID='19307090001';


# 1.4 student
select IOTime, IOType
from IOLog
where studentID='19307090001'
order by IOTime;
# hand to java

# 2.1
/*过去 n 天尚未批准的入校申请和出校申请数量及详细信息；*/
SELECT * FROM entryApplication WHERE progress='submitted' AND datediff(curdate(), date(applyTime)) < 5;
SELECT * FROM leaveApplication WHERE progress='submitted' AND datediff(curdate(), date(applyTime)) < 5;

# 2.2
/*前 n 个提交入校申请最多的学生，支持按多级范围（全校、院系、班级）进行筛选；*/
# university
SELECT studentId, count(ID) as applicationAmount
FROM entryApplication
GROUP BY studentId
order by applicationAmount desc
limit 0,n;

# department
SELECT studentId, count(ID) as applicationAmount
FROM entryApplication
where (select classID from student where ID=studentId)
          in (select ID from class where dptID='1' )
GROUP BY studentId
order by applicationAmount desc
limit 0,1;


# class
SELECT studentId, count(ID) as applicationAmount
FROM entryApplication
where studentId
in (select ID from student where classID = '1')
GROUP BY studentId
order by applicationAmount desc
limit 0,1;

# 2.3
# reuse 1.4, java to finish this
# university
select studentId
from student;
# department
select studentId
from student
where (select classID from student where ID=studentId)
          in (select ID from class where dptID='1' );
# class
select studentId
from student
where studentId
          in (select ID from student where classID = '1');


# 2.4
select count(distinct ID) as NumberOfOutStudent
from student
where inschool = 0;

select max(IOTime)
from IOLog
where studentID in (select ID from student where inschool = 0)
group by studentID

select *
from student
where inschool=0;

# 2.5



# 2.6



# 2.7



# 2.8



# 2.9

