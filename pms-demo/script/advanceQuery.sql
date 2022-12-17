/*过去 n 天尚未批准的入校申请和出校申请数量及详细信息；*/ 
SELECT * FROM entryApplication WHERE state='submitted' AND applyTime > startDay;
SELECT * FROM leaveApplication WHERE state='submitted' AND applyTime > startDay;
/*前 n 个提交入校申请最多的学生，支持按多级范围（全校、院系、班级）进行筛选；*/
SELECT studentId, count(ID) as applycationAmount FROM entryApplication GROUP BY studentId order by applycationAmount desc, studentId asc;

select student.studentId as in_school_not_apply_leave 
from student 
where entryPerm > 0 and student.studentID = not_apply_leave.studentID;

(select distinct studentID  
from student
where entryPerm > 0 )
except
(
    select studentID 
    from leaveApplication
    where progress='submitted' or progress='approved'
)